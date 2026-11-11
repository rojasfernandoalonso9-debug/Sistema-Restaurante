import { describe, expect, it } from 'vitest';
import { strFromU8, unzipSync } from 'fflate';
import { buildReportWorkbook } from './reportXlsx';

describe('report workbook', () => {
  it('creates a valid xlsx package with escaped product names and summary sheets', () => {
    const file = buildReportWorkbook({ period: 'DAILY', from: '2026-01-01', to: '2026-01-01', completedSales: 2, revenue: 28.5, averageSale: 14.25, bestSellingProducts: [{ name: 'Café & <pan>', quantity: 2, revenue: 28.5 }] });
    const entries = unzipSync(file);
    expect(Object.keys(entries)).toContain('[Content_Types].xml');
    expect(strFromU8(entries['xl/workbook.xml'])).toContain('name="Resumen"');
    const products = strFromU8(entries['xl/worksheets/sheet1.xml']);
    expect(products).toContain('Café &amp; &lt;pan&gt;');
    expect(products).toContain('<v>28.5</v>');
    expect(strFromU8(entries['xl/worksheets/sheet2.xml'])).toContain('Ventas completadas');
  });
});
