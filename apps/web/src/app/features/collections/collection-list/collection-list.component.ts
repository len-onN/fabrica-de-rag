import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { CollectionService, CollectionResponse } from '../../../core/http/collection.service';

@Component({
  selector: 'app-collection-list',
  imports: [CommonModule, RouterLink],
  templateUrl: './collection-list.component.html',
  styleUrls: ['./collection-list.component.css']
})
export class CollectionListComponent implements OnInit {
  private collectionService = inject(CollectionService);

  collections: CollectionResponse[] = [];
  loading = true;
  error = '';

  ngOnInit() {
    this.loadCollections();
  }

  private loadCollections() {
    this.collectionService.getCollections().subscribe({
      next: (response) => {
        this.collections = response.data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Falha ao carregar as coleções.';
        this.loading = false;
        console.error(err);
      }
    });
  }
}
