export function compareVersion(v1: string, symbolAndVersion: string): boolean {
  const match = symbolAndVersion.match(/(>=|<=|>|<|=)?\s*(.*)/);
  if (!match) throw new Error(`Invalid version comparison: ${symbolAndVersion}`);
  const [, symbol = '=', v2] = match;
  const parts1 = v1.split('.').map(Number);
  const parts2 = v2!.split('.').map(Number);
  const len = Math.max(parts1.length, parts2.length);
  for (let i = 0; i < len; i++) {
    const p1 = parts1[i] || 0;
    const p2 = parts2[i] || 0;
    if (p1 !== p2) {
      switch (symbol) {
        case '>':
          return p1 > p2
        case '<':
          return p1 < p2
        case '>=':
          return p1 >= p2
        case '<=':
          return p1 <= p2
        case '=':
        default:
          return p1 > p2
      }
    }
  }
  return symbol === '=' || symbol === '>=' || symbol === '<='
}

