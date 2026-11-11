export function validateRecordPayload(payload: Record<string, FormDataEntryValue>): string | null {
  if (typeof payload.name !== 'string' || payload.name.trim().length === 0) return 'El nombre es obligatorio.';
  for (const field of ['price', 'stock', 'capacity']) {
    const value = payload[field];
    if (typeof value === 'string' && value.trim() !== '' && (!Number.isFinite(Number(value)) || Number(value) < 0)) {
      return `El campo ${field} debe ser un número igual o mayor que cero.`;
    }
  }
  return null;
}
