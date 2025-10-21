import fs from 'fs/promises';
import path from 'path';
import {cachedFetch, fetchModrinth} from "./libs/fetch.ts";
import {compareVersion} from "./libs/version.ts";

type VersionResolver = (version: string, loader: string) => Promise<string | null> | string | null;

const PROJECT_ROOT = './';
const MODS: Record<'shared' | 'neoforge' | 'fabric', Record<string, VersionResolver>> = {
  shared: {
    'deps.balm': modrinthResolver('balm', true),
    'deps.waystones': modrinthResolver('waystones'),
    'deps.xaeros_minimap': modrinthResolver('xaeros-minimap'),
    'deps.yacl': (version, loader) =>
        compareVersion(version, ">=1.20.3")
            ? modrinthResolver('yacl', true)(version, loader)
            : null
    // TODO: add deps.parchment
  },
  neoforge: {
    'modstitch.platform': () => 'moddevgradle',
    'deps.neoforge': async version => {
      const [, minor = 0, patch = 0] = version.split('.').map(Number);
      const json: {
        versions: string[];
      } = await cachedFetch('https://maven.neoforged.net/api/maven/versions/releases/net%2Fneoforged%2Fneoforge')
      .then(v => JSON.parse(v));

      const neoVersion = json.versions.findLast(v => v.startsWith(`${minor}.${patch}.`));
      if (!neoVersion) throw new Error(`No compatible neoforge version found for ${version}`);
      return neoVersion;
    },
    'deps.kotlin': modrinthResolver('kotlin-for-forge'),
  },
  fabric: {
    'modstitch.platform': () => 'loom',
    'deps.fabric_api': modrinthResolver('fabric-api'),
    'deps.modmenu': modrinthResolver('modmenu'),
  },
};

for (const vl of await fs.readdir(path.resolve(PROJECT_ROOT, 'versions'))) {
  const [version, loader] = vl.split('-');
  if (!version || !loader) {
    console.warn(`Skipping invalid version directory: ${vl}`);
    continue;
  }

  const dir = path.resolve(PROJECT_ROOT, 'versions', vl);
  if (!(await fs.stat(dir)).isDirectory() || dir.startsWith('.')) continue;
  for (const [name, mods] of Object.entries(MODS)) {
    if (
        name !== 'shared' && name !== loader
    ) {
      continue;
    }
    const propertiesPath = path.resolve(dir, `gradle.properties`);
    for (const [mod, resolver] of Object.entries(mods)) {
      const depVersion = await resolver(version, loader);
      if (depVersion) {
        await writeToProperties(propertiesPath, {
          [mod]: depVersion,
        });
      } else {
        if (depVersion === null) {
          console.warn(`Skipping optional mod ${mod} for ${vl}`);
          continue;
        }
        throw new Error(`Failed to resolve version for ${mod} in ${vl}`);
      }
    }
  }
}

async function writeToProperties(file: string, properties: Record<string, string>) {
  const old = await fs.readFile(file, 'utf-8').catch(() => '');
  const lines = old.split('\n').filter(l => l.trim());
  const map: Record<string, string> = {};
  for (const line of lines) {
    const [k, ...v] = line.split('=');
    map[k!.trim()] = v.join('=').trim();
  }
  for (const k of Object.keys(properties))
    map[k] = properties[k]!;
  const content = Object.entries(map).map(([k, v]) => `${k}=${v}`).join('\n') + '\n';
  await fs.writeFile(file, content, 'utf-8');
}

function modrinthResolver(slug: string, optional?: boolean): VersionResolver {
  return async (version, loader) => {
    const params = new URLSearchParams();
    params.set('loaders', JSON.stringify([loader]));
    params.set('game_versions', JSON.stringify([version]));
    const res = JSON.parse(await fetchModrinth(`/project/${slug}/version?${params.toString()}`));

    console.log(slug, version, loader);

    const version_number = res[0]?.version_number;
    if (!version_number) {
      if (optional) return null;
      throw new Error(`No version found for ${slug} on ${loader} ${version}`);
    }
    return version_number;
  };
}
