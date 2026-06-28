import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CollectionService } from '../../../core/http/collection.service';
import { PageNumberingService, DocumentPageDto, PageNumberingAnchorDto } from '../../../core/http/page-numbering.service';

@Component({
  selector: 'app-document-detail',
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './document-detail.component.html',
  styleUrls: ['./document-detail.component.css']
})
export class DocumentDetailComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private collectionService = inject(CollectionService);
  private pageNumberingService = inject(PageNumberingService);
  
  collectionId = this.route.snapshot.paramMap.get('id');
  documentId = this.route.snapshot.paramMap.get('docId');
  workspaceId: string | null = null;
  
  activeTab = 'pages';
  pages: DocumentPageDto[] = [];
  anchors: PageNumberingAnchorDto[] = [];
  loading = false;
  
  showAnchorModal = false;
  selectedPage: DocumentPageDto | null = null;
  newAnchor: Partial<PageNumberingAnchorDto> = {};

  ngOnInit() {
    if (this.collectionId) {
      this.collectionService.getCollection(this.collectionId).subscribe(col => {
        this.workspaceId = col.workspaceId;
        this.loadPages();
      });
    }
  }
  
  setTab(tab: string) {
    this.activeTab = tab;
  }
  
  loadPages() {
    if (!this.workspaceId || !this.documentId) return;
    this.loading = true;
    this.pageNumberingService.getPages(this.workspaceId, this.documentId).subscribe(res => {
      this.pages = res;
      this.loading = false;
    });
    this.pageNumberingService.getAnchors(this.workspaceId, this.documentId).subscribe(res => {
      this.anchors = res;
    });
  }
  
  openAnchorModal(page: DocumentPageDto) {
    this.selectedPage = page;
    const existing = this.anchors.find(a => a.filePageNumber === page.filePageNumber);
    if (existing) {
      this.newAnchor = { ...existing };
    } else {
      this.newAnchor = {
        filePageNumber: page.filePageNumber,
        printedLabel: page.detectedPrintedLabel || String(page.filePageNumber),
        numberingStyle: 'arabic',
        applyDirection: 'forward'
      };
    }
    this.showAnchorModal = true;
  }
  
  closeModal(event: Event) {
    this.showAnchorModal = false;
  }
  
  saveAnchor() {
    if (!this.workspaceId || !this.documentId || !this.selectedPage) return;
    
    // Remove old anchor for this page if exists, then add new one
    const newAnchors = this.anchors.filter(a => a.filePageNumber !== this.selectedPage!.filePageNumber);
    newAnchors.push(this.newAnchor as PageNumberingAnchorDto);
    
    this.pageNumberingService.updateAnchors(this.workspaceId, this.documentId, { anchors: newAnchors }).subscribe(() => {
      this.showAnchorModal = false;
      this.loadPages();
    });
  }
}
