import fs from 'fs/promises';
import path from 'path';

const CACHE_ROOT = './node_modules/.scripts-cache/';

/**
 * @param url
 * @param ttl Time to live in milliseconds
 * @param options
 */
export async function cachedFetch(
    url: string,
    ttl: number = 1000 * 60 * 60, // 1 hour
    options?: RequestInit,
) {
  const hash = Buffer.from(url).toString('base64').replace(/[/+=]/g, '_');
  const cacheFile = path.resolve(CACHE_ROOT, hash);

  if (await fs.exists(cacheFile)) {
    const cache = await fs.readFile(cacheFile, 'utf-8');
    const [timestampLine, ...dataLines] = cache.split('\n');
    const timestamp = Number(timestampLine);
    const data = dataLines.join('\n');

    if (
        !isNaN(timestamp) &&
        (Date.now() - timestamp) < ttl
    ) {
      return data;
    } else {
      console.log(`Cache expired for ${url}, refetching...`);
    }
  }

  const res = await fetch(url, options);
  if (!res.ok) throw new Error(`Failed to fetch ${url}: ${res.status} ${res.statusText}`);
  const text = await res.text();
  await fs.mkdir(CACHE_ROOT, {recursive: true});
  await fs.writeFile(cacheFile, `${Date.now()}\n${text}`, 'utf-8');
  return text;
}

export async function fetchModrinth(path: string) {
  return cachedFetch(`https://api.modrinth.com/v2${path}`);
}