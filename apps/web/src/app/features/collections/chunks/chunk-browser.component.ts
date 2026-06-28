import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChunkService, ChunkResponse } from '../../../core/http/chunk.service';
import { ChunkDetailsPanelComponent } from './chunk-details-panel.component';

@Component({
  selector: 'app-chunk-browser',
  standalone: true,
  imports: [CommonModule, FormsModule, ChunkDetailsPanelComponent],
  templateUrl: './chunk-browser.component.html',
  styleUrls: ['./chunk-browser.component.css']
})
export class ChunkBrowserComponent implements OnInit {
  @Input() workspaceId!: string;
  @Input() collectionId!: string;
  @Input() documentId?: string; // Optional filter

  private chunkService = inject(ChunkService);

  chunks: ChunkResponse[] = [];
  totalElements = 0;
  totalPages = 0;
  page = 0;
  size = 15;
  loading = false;
  searchQuery = '';

  selectedChunk: ChunkResponse | null = null;

  ngOnInit() {
    this.loadChunks();
  }

  loadChunks() {
    this.loading = true;
    this.chunkService.getChunks(this.workspaceId, this.collectionId, this.documentId, this.searchQuery, this.page, this.size)
      .subscribe({
        next: (res) => {
          this.chunks = res.content;
          this.totalElements = res.totalElements;
          this.totalPages = res.totalPages;
          this.loading = false;
        },
        error: (err) => {
          console.error(err);
          this.loading = false;
        }
      });
  }

  onSearch() {
    this.page = 0;
    this.loadChunks();
  }

  nextPage() {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadChunks();
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadChunks();
    }
  }

  selectChunk(chunk: ChunkResponse) {
    this.selectedChunk = chunk;
  }

  closePanel() {
    this.selectedChunk = null;
  }

  parseHeading(headingJson: string): string {
    try {
      if (!headingJson) return '-';
      const parsed = JSON.parse(headingJson);
      if (Array.isArray(parsed) && parsed.length > 0) {
        return parsed.join(' > ');
      }
      return '-';
    } catch {
      return headingJson || '-';
    }
  }
}
