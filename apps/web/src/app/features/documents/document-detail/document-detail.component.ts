import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  selector: 'app-document-detail',
  imports: [CommonModule, RouterLink],
  templateUrl: './document-detail.component.html',
  styleUrls: ['./document-detail.component.css']
})
export class DocumentDetailComponent {
  private route = inject(ActivatedRoute);
  
  collectionId = this.route.snapshot.paramMap.get('id');
  documentId = this.route.snapshot.paramMap.get('docId');
}
