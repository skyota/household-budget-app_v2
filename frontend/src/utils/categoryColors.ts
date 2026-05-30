export const CATEGORY_PALETTE = [
  '#0066cc',
  '#34c759',
  '#ff9500',
  '#af52de',
  '#5ac8fa',
  '#ffcc00',
  '#00c7be',
  '#bf5af2',
  '#30b0c7',
  '#ff6b00',
]

export function getCategoryColor(categoryId: number | null, orderedIds: number[]): string | null {
  if (categoryId === null) return null
  const index = orderedIds.indexOf(categoryId)
  return index === -1 ? null : CATEGORY_PALETTE[index % CATEGORY_PALETTE.length]
}
