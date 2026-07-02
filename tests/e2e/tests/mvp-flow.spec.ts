import { test, expect } from '@playwright/test';
import path from 'path';

test('MVP E2E Flow: Bootstrap, Ingestão e RAG', async ({ page }) => {
  test.setTimeout(120000); // 2 minutes for E2E flow including upload and ingestion

  // 1. Boot & Bootstrap
  await page.goto('/');
  
  // Verifica se estamos na página de bootstrap
  await expect(page.getByText('Configuração inicial do sistema')).toBeVisible();

  // Preenche dados do admin
  await page.getByLabel('Nome de Exibição').fill('Admin E2E');
  await page.getByLabel('E-mail').fill('admin@e2e.local');
  await page.getByLabel('Senha').fill('senha-segura-e2e');
  await page.getByLabel('Nome da Organização').fill('Workspace E2E');
  
  await page.getByRole('button', { name: 'Iniciar Sistema' }).click();

  // Espera redirecionar para o dashboard do workspace
  await expect(page.getByText('Workspace E2E')).toBeVisible();
  await expect(page.getByText('Coleções')).toBeVisible();

  // 2. Criar Coleção
  await page.getByRole('link', { name: 'Coleções' }).click();
  // Supõe-se que exista um botão "Nova Coleção" ou similar na lista
  await page.getByRole('button', { name: 'Nova Coleção' }).click();
  
  await page.getByLabel('Nome da Coleção').fill('Documentos de Teste');
  await page.getByLabel('Descrição').fill('Coleção para testes E2E automatizados');
  await page.getByRole('button', { name: 'Criar' }).click();

  // Redireciona para o detalhe da coleção
  await expect(page.getByText('Documentos de Teste')).toBeVisible();

  // 3. Upload de Documento
  // Localiza o input de arquivo oculto associado ao botão de upload
  const fileChooserPromise = page.waitForEvent('filechooser');
  await page.getByRole('button', { name: /Upload/i }).click();
  const fileChooser = await fileChooserPromise;
  
  const fixturePath = path.resolve(__dirname, '../../fixtures/e2e-test-doc.pdf');
  await fileChooser.setFiles(fixturePath);

  // Aguarda a interface indicar que o arquivo subiu e a run iniciou
  await expect(page.getByText('e2e-test-doc.pdf')).toBeVisible();

  // 4. Aguardar Ingestão
  // A UI deve mostrar "COMPLETED" ou "Concluído" quando a pipeline termina
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
});
