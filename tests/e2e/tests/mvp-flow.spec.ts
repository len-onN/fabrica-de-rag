import { test, expect } from '@playwright/test';
import path from 'path';

test('MVP E2E Flow: Bootstrap, Ingestão e RAG', async ({ page }) => {
  test.setTimeout(120000); // 2 minutes for E2E flow including upload and ingestion

  // Inicia o fluxo a partir da tela de bootstrap
  await page.goto('/auth/bootstrap');
  
  // Verifica se estamos na página de bootstrap
  await expect(page.getByText('Configuração inicial e criação da sua conta')).toBeVisible();

  // Preenche dados do admin
  await page.getByLabel('Nome de Exibição').fill('Admin E2E');
  await page.getByLabel('E-mail').fill('admin@e2e.local');
  await page.getByLabel('Senha').fill('SenhaForte123!@#');
  await page.getByLabel('Nome do Workspace').fill('Workspace E2E');
  
  await page.getByRole('button', { name: 'Iniciar Sistema' }).click();

  // Espera redirecionar para o dashboard do workspace
  await expect(page.getByText('Workspace E2E')).toBeVisible({ timeout: 10000 });
  
  // =====================================================================
  // OBS: As etapas abaixo (Coleções, Upload e RAG) estão comentadas 
  // pois as respectivas telas UI ainda não foram desenvolvidas!
  // Elas serão reativadas quando os módulos de frontend estiverem prontos.
  // =====================================================================
  
  /*
  await expect(page.getByText('Coleções')).toBeVisible();

  // 2. Criar Coleção
  await page.getByRole('link', { name: 'Coleções' }).click();
  await page.getByRole('button', { name: 'Nova Coleção' }).click();
  
  await page.getByLabel('Nome da Coleção').fill('Documentos de Teste');
  await page.getByLabel('Descrição').fill('Coleção para testes E2E automatizados');
  await page.getByRole('button', { name: 'Criar' }).click();

  await expect(page.getByText('Documentos de Teste')).toBeVisible();

  // 3. Upload de Documento
  const fileChooserPromise = page.waitForEvent('filechooser');
  await page.getByRole('button', { name: /Upload/i }).click();
  const fileChooser = await fileChooserPromise;
  
  const fixturePath = path.resolve(__dirname, '../../fixtures/e2e-test-doc.pdf');
  await fileChooser.setFiles(fixturePath);

  await expect(page.getByText('e2e-test-doc.pdf')).toBeVisible();

  // 4. Aguardar Ingestão
  await expect(page.locator('text=COMPLETED').or(page.locator('text=Concluído'))).toBeVisible({ timeout: 45000 });

  // 5. Laboratório RAG e Busca
  await page.getByRole('tab', { name: /Laboratório/i }).or(page.getByRole('button', { name: /Laboratório/i })).click();
  
  await page.getByPlaceholder(/Faça uma pergunta/i).fill('Qual a função do Python Worker?');
  await page.getByRole('button', { name: 'Buscar' }).or(page.getByRole('button', { name: 'Enviar' })).click();

  // 6. Validações do Contexto e Resposta
  await expect(page.getByText(/Extração e Chunking Visual/i)).toBeVisible();
  
  // Feedback
  const feedbackBtn = page.getByRole('button', { name: /👍/i }).first();
  await expect(feedbackBtn).toBeVisible();
  await feedbackBtn.click();
  */
});
