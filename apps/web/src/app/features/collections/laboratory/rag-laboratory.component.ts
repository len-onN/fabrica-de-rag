import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RagService, AskRequest, AskResponse, RetrievalFeedbackRequest } from '../../../core/http/rag.service';

@Component({
  selector: 'app-rag-laboratory',
  imports: [CommonModule, FormsModule],
  templateUrl: './rag-laboratory.component.html',
  styleUrls: ['./rag-laboratory.component.css']
})
export class RagLaboratoryComponent {
  @Input() workspaceId!: string;
  @Input() collectionId!: string;

  private ragService = inject(RagService);

  query = '';
  topK = 8;
  includeTables = true;
  includeVisuals = false;
  tokenBudget = 5000;
  
  loading = false;
  error = '';
  response: AskResponse | null = null;
  feedbackSubmitted = false;

  activeTab: 'response' | 'chunks' | 'context' = 'response';

  ask() {
    if (!this.query.trim()) return;
    
    this.loading = true;
    this.error = '';
    this.response = null;
    this.feedbackSubmitted = false;
    this.activeTab = 'response';

    const req: AskRequest = {
      workspaceId: this.workspaceId,
      collectionId: this.collectionId,
      query: this.query,
      topK: this.topK,
      includeTables: this.includeTables,
      includeVisuals: this.includeVisuals,
      tokenBudget: this.tokenBudget
    };

    this.ragService.ask(this.workspaceId, req).subscribe({
      next: (res) => {
        this.response = res;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.title || 'Erro ao realizar a busca RAG.';
        this.loading = false;
      }
    });
  }

  submitFeedback(type: 'positive' | 'negative') {
    if (!this.response) return;

    const req: RetrievalFeedbackRequest = {
      collectionId: this.collectionId,
      queryEventId: this.response.queryId,
      feedbackType: type
    };

    this.ragService.submitFeedback(this.workspaceId, req).subscribe({
      next: () => {
        this.feedbackSubmitted = true;
      },
      error: (err) => {
        console.error('Feedback failed', err);
        alert('Erro ao enviar feedback.');
      }
    });
  }
}
