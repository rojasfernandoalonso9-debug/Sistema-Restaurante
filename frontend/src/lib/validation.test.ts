import { describe, expect, it } from 'vitest';
import { validateRecordPayload } from './validation';
describe('validateRecordPayload', () => {
  it('requires a non-empty name', () => { expect(validateRecordPayload({ name: '  ' })).toContain('obligatorio'); });
  it('rejects negative quantities and prices', () => { expect(validateRecordPayload({ name: 'Arroz', stock: '-1' })).toContain('igual o mayor'); expect(validateRecordPayload({ name: 'Menú', price: '-0.01' })).toContain('igual o mayor'); });
  it('accepts a valid product form', () => { expect(validateRecordPayload({ name: 'Menú', price: '18.50' })).toBeNull(); });
});
