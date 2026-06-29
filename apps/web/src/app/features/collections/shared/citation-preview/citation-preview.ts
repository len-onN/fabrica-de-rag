import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Citation } from '../../../../core/http/rag.service';

@Component({
  selector: 'app-citation-preview',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './citation-preview.html',
  styleUrl: './citation-preview.css'
})
export class CitationPreviewComponent {
  @Input() citation: Citation | null = null;
  @Input() workspaceId!: string;
  @Output() closePanel = new EventEmitter<void>();

  get assetUrl(): string {
    if (!this.citation || !this.citation.assetId) return '';
    return `/api/v1/workspaces/${this.workspaceId}/documents/${this.citation.documentId}/assets/${this.citation.assetId}`;
  }

  get highlightStyle(): any {
    if (!this.citation || !this.citation.sourceLocator) return null;
    const pts = this.citation.sourceLocator['pdf_points_top_left'];
    if (!pts || !Array.isArray(pts) || pts.length < 4) return null;
    return {
      left: `${pts[0] * 100}%`,
      top: `${pts[1] * 100}%`,
      width: `${pts[2] * 100}%`,
      height: `${pts[3] * 100}%`
    };
  }

  onClose() {
    this.closePanel.emit();
  }
}
