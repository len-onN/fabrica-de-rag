import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Citation } from '../../../../core/http/rag.service';

@Component({
  selector: 'app-citation-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './citation-card.html',
  styleUrl: './citation-card.css'
})
export class CitationCardComponent {
  @Input({ required: true }) citation!: Citation;
  @Output() viewPreview = new EventEmitter<Citation>();

  onViewClick() {
    this.viewPreview.emit(this.citation);
  }
}
