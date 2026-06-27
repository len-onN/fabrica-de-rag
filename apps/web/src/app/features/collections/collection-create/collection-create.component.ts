import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CollectionService } from '../../../core/http/collection.service';

@Component({
  selector: 'app-collection-create',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './collection-create.component.html',
  styleUrls: ['./collection-create.component.css']
})
export class CollectionCreateComponent {
  private fb = inject(FormBuilder);
  private collectionService = inject(CollectionService);
  private router = inject(Router);

  createForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(255)]],
    description: ['', [Validators.maxLength(1000)]],
    purpose: ['general', [Validators.required]],
    defaultIngestionProfile: ['balanced', [Validators.required]],
    defaultContextPolicy: ['conservative', [Validators.required]]
  });

  submitting = false;
  error = '';

  onSubmit() {
    if (this.createForm.invalid) {
      return;
    }

    this.submitting = true;
    this.error = '';

    const payload = {
      name: this.createForm.value.name!,
      description: this.createForm.value.description || '',
      purpose: this.createForm.value.purpose!,
      defaultIngestionProfile: this.createForm.value.defaultIngestionProfile!,
      defaultContextPolicy: this.createForm.value.defaultContextPolicy!
    };

    this.collectionService.createCollection(payload).subscribe({
      next: (res) => {
        this.router.navigate(['/collections', res.id]);
      },
      error: (err) => {
        this.submitting = false;
        this.error = 'Falha ao criar coleção. Verifique os dados.';
        console.error(err);
      }
    });
  }
}
