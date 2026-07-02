import { test, expect } from '@playwright/test';

test.describe('Autenticação e Conexão com o Banco', () => {
  test('Deve rejeitar login inválido sem erro 500 (Garante que a API conecta ao DB)', async ({ page }) => {
    // Escutamos as respostas da API para garantir que não temos 500
    const responsePromise = page.waitForResponse(response => response.url().includes('/api/v1/auth/login'));
    
    page.on('console', msg => console.log('BROWSER CONSOLE:', msg.text()));
    page.on('pageerror', err => console.log('BROWSER ERROR:', err.message));

    await page.goto('/auth/login');
    
    await page.getByLabel('E-mail').fill('invalido@e2e.local');
    await page.getByLabel('Senha').fill('senha-invalida');
    await page.getByRole('button', { name: 'Entrar' }).click();
    
    const response = await responsePromise;
    console.log('HTTP STATUS:', response.status());
    console.log('HTTP BODY:', await response.text());
    expect(response.status()).not.toBe(500); // Se for 500, falhou a conexão DB
    expect(response.status()).toBe(400); // O esperado para credenciais erradas (Business Exception)
    
    // Verifica se a mensagem de erro esperada aparece na UI
    await expect(page.getByText('E-mail ou senha inválidos.')).toBeVisible();
  });


});
