import { Component, Input, Output, EventEmitter, inject, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChunkService, ChunkResponse } from '../../../core/http/chunk.service';

@Component({
  selector: 'app-chunk-details-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './chunk-details-panel.component.html',
  styleUrls: ['./chunk-details-panel.component.css']
})
export class ChunkDetailsPanelComponent implements OnChanges {
  @Input() chunk!: ChunkResponse;
  @Input() workspaceId!: string;
  @Input() collectionId!: string;
  
  @Output() closed = new EventEmitter<void>();
  @Output() chunkChanged = new EventEmitter<ChunkResponse>();

  private chunkService = inject(ChunkService);

  parsedLocator: any = null;
  parsedHeading: string[] = [];
  
  feedbackSubmitting = false;
  feedbackGiven: 'chunk_relevant' | 'chunk_irrelevant' | null = null;

  ngOnChanges(changes: SimpleChanges) {
    if (changes['chunk'] && this.chunk) {
      this.parseMetadata();
      this.feedbackGiven = null; // reset on chunk change
    }
  }

  private parseMetadata() {
    try {
      this.parsedLocator = this.chunk.sourceLocator ? JSON.parse(this.chunk.sourceLocator) : null;
    } catch (e) {
      this.parsedLocator = null;
    }
    
    try {
      this.parsedHeading = this.chunk.headingPath ? JSON.parse(this.chunk.headingPath) : [];
      if (!Array.isArray(this.parsedHeading)) this.parsedHeading = [];
    } catch (e) {
      this.parsedHeading = [];
    }
  }

  close() {
    this.closed.emit();
  }

  loadNeighbor(offset: number) {
    this.chunkService.getNeighbor(this.workspaceId, this.collectionId, this.chunk.publicId, offset)
      .subscribe({
        next: (neighbor) => {
          this.chunkChanged.emit(neighbor);
        },
        error: (err) => {
          if (err.status === 404) {
            alert('Vizinho não encontrado (fim do documento ou não indexado).');
          } else {
            console.error(err);
          }
        }
      });
  }

  submitFeedback(isRelevant: boolean) {
    if (this.feedbackSubmitting || this.feedbackGiven) return;
    
    this.feedbackSubmitting = true;
    const type = isRelevant ? 'chunk_relevant' : 'chunk_irrelevant';
    
    this.chunkService.submitFeedback(this.workspaceId, {
      collectionId: this.collectionId,
      documentId: this.chunk.documentId,
      chunkId: this.chunk.publicId,
      feedbackType: type
    }).subscribe({
      next: () => {
        this.feedbackGiven = type;
        this.feedbackSubmitting = false;
      },
      error: (err) => {
        console.error('Failed to submit feedback', err);
        this.feedbackSubmitting = false;
        alert('Erro ao enviar feedback.');
      }
    });
  }
}
