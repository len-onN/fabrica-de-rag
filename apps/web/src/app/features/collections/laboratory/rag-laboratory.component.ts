import { Component, Input, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RagService, AskRequest, AskResponse, RetrievalFeedbackRequest, Citation } from '../../../core/http/rag.service';
import { AnalyticsService, AnalyticsEventRequest } from '../../../core/http/analytics.service';
import { CitationCardComponent } from '../shared/citation-card/citation-card';
import { CitationPreviewComponent } from '../shared/citation-preview/citation-preview';

@Component({
  selector: 'app-rag-laboratory',
  standalone: true,
  imports: [CommonModule, FormsModule, CitationCardComponent, CitationPreviewComponent],
  templateUrl: './rag-laboratory.component.html',
  styleUrls: ['./rag-laboratory.component.css']
})
export class RagLaboratoryComponent implements OnInit {
  @Input() workspaceId!: string;
  @Input() collectionId!: string;

  private ragService = inject(RagService);
  private analyticsService = inject(AnalyticsService);

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
  selectedCitation: Citation | null = null;

  ngOnInit() {
    if (this.workspaceId) {
      const req: AnalyticsEventRequest = {
        eventVersion: 'analytics.event.v1',
        eventName: 'retrieval_lab_opened',
        origin: 'ui',
        retentionClass: 'analytics',
        correlationId: 'req_' + Math.random().toString(36).substring(2, 10),
        occurredAt: new Date().toISOString(),
        resource: { type: 'workspace', id: this.workspaceId },
        properties: { collectionId: this.collectionId }
      };
      this.analyticsService.recordEvent(this.workspaceId, req).subscribe();
    }
  }

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
    if (!this.response || !this.collectionId) return;

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

  openPreview(citation: Citation) {
    this.selectedCitation = citation;
  }

  closePreview() {
    this.selectedCitation = null;
  }
}
