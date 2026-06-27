import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CollectionService, CollectionResponse } from '../../../core/http/collection.service';

@Component({
  selector: 'app-collection-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './collection-detail.component.html',
  styleUrls: ['./collection-detail.component.css']
})
export class CollectionDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private collectionService = inject(CollectionService);

  collection: CollectionResponse | null = null;
  loading = true;
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
      },
      error: (err) => {
        this.error = 'Coleção não encontrada.';
        this.loading = false;
        console.error(err);
      }
    });
  }

  setTab(tabId: string) {
    this.activeTab = tabId;
  }

  getActiveTabLabel(): string {
    const tab = this.tabs.find(t => t.id === this.activeTab);
    return tab ? tab.label : '';
  }
}
