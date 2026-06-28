import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CollectionService, CollectionResponse } from '../../../core/http/collection.service';
import { DocumentService, DocumentResponse } from '../../../core/http/document.service';

@Component({
  selector: 'app-collection-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './collection-detail.component.html',
  styleUrls: ['./collection-detail.component.css']
})
export class CollectionDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private collectionService = inject(CollectionService);
  private documentService = inject(DocumentService);

  collection: CollectionResponse | null = null;
  documents: DocumentResponse[] = [];
  
  loading = true;
  loadingDocs = false;
  uploading = false;
  error = '';
  activeTab = 'documents';

  tabs = [
    { id: 'documents', label: 'Documentos' },
    { id: 'chunks', label: 'Chunks' },
    { id: 'jobs', label: 'Jobs' },
    { id: 'lab', label: 'Laboratório' },
    { id: 'analytics', label: 'Analytics' },
    { id: 'settings', label: 'Configurações' }
  ];

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (id) {
        this.loadCollection(id);
      }
    });
  }

  private loadCollection(id: string) {
    this.loading = true;
    this.collectionService.getCollection(id).subscribe({
      next: (res) => {
        this.collection = res;
        this.loading = false;
        this.loadDocuments();
      },
      error: (err) => {
        this.error = 'Coleção não encontrada.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  private loadDocuments() {
    if (!this.collection) return;
    this.loadingDocs = true;
    this.documentService.getDocuments(this.collection.workspaceId, this.collection.id).subscribe({
      next: (page) => {
        this.documents = page.content;
        this.loadingDocs = false;
      },
      error: (err) => {
        console.error('Failed to load documents', err);
        this.loadingDocs = false;
      }
    });
  }

  triggerUpload(fileInput: HTMLInputElement) {
    fileInput.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0 && this.collection) {
      const file = input.files[0];
      
      if (file.type !== 'application/pdf') {
        alert('Por favor, selecione um arquivo PDF.');
        input.value = '';
        return;
      }
      
      if (file.size > 90 * 1024 * 1024) {
        alert('O arquivo excede o limite de 90MB.');
        input.value = '';
        return;
      }

      this.uploading = true;
      this.documentService.uploadDocument(this.collection.workspaceId, this.collection.id, file).subscribe({
        next: (doc) => {
          this.uploading = false;
          this.documents.unshift(doc);
          input.value = '';
        },
        error: (err) => {
          console.error('Upload failed', err);
          alert('Erro ao fazer upload do documento.');
          this.uploading = false;
          input.value = '';
        }
      });
    }
  }

  setTab(tabId: string) {
    this.activeTab = tabId;
  }

  getActiveTabLabel(): string {
    const tab = this.tabs.find(t => t.id === this.activeTab);
    return tab ? tab.label : '';
  }
}
