import {ModrinthV2Client} from "@xmcl/modrinth"

const config = {
  slugs: [
    ["waystones", "🪦"],
    ["xaeros-minimap", "🗺️"],
  ],
  loaders: [
    "fabric",
    "forge",
    "neoforge"
  ]
}

const versionsMap: Record<string, Record<string, string[]>> = {}

const client = new ModrinthV2Client()

for (const [slug, icon] of config.slugs) {
  if (!slug || !icon) continue
  const versions = await client.getProjectVersions(slug)

  for (const version of versions) {
    for (const loader of version.loaders) {
      for (const gameVersion of version.game_versions) {
        if (config.loaders.includes(loader)) {
          versionsMap[gameVersion] ??= {}
          versionsMap[gameVersion][loader] ??= []
          if (!versionsMap[gameVersion][loader].includes(icon)) {
            versionsMap[gameVersion][loader].push(icon)
          }
        }
      }
    }
  }
}

console.log("|", "Minecraft Version", "|", config.loaders.map(l => l.charAt(0).toUpperCase() + l.slice(1)).join(" | "), "|")
console.log("|", "-".repeat(17), "|", config.loaders.map(() => "-".repeat(10)).join(" | "), "|")

for (const gameVersion of Object.keys(versionsMap).sort((a, b) => {
  const [aMajor, aMinor = "0", aPatch = "0"] = a.split(".")
  const [bMajor, bMinor = "0", bPatch = "0"] = b.split(".")
  return parseInt(bMajor!) - parseInt(aMajor!) || parseInt(bMinor) - parseInt(aMinor) || parseInt(bPatch) - parseInt(aPatch)
})) {
  console.log("|", gameVersion, "|", config.loaders.map(loader => versionsMap[gameVersion]![loader]?.length ? versionsMap[gameVersion]![loader].join(", ") : "-").join(" | "), "|")
}