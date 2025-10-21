import fs from "fs/promises"

const properties = await fs.readFile("gradle.properties", "utf-8")
const newProperties = properties.replace(
    /mod\.version=.*/g,
    `mod.version=${process.argv[2]}`
)
await fs.writeFile("gradle.properties", newProperties)