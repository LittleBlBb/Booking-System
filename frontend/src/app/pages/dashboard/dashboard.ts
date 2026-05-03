import { Component, OnInit, ChangeDetectorRef, CUSTOM_ELEMENTS_SCHEMA, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, Me } from '../../core/services/auth.service';
import { ResourceService, Resource } from '../../core/services/resource.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './dashboard.html',
})
export class DashboardComponent implements OnInit {

  me: Me | null = null;
  resources: Resource[] = [];
  filteredResources: Resource[] = [];

  searchQuery = '';
  selectedType = '';
  allTypes: string[] = [];

  loading = true;

  constructor(
    private router: Router,
    private authService: AuthService,
    private resourceService: ResourceService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit() {
    setTimeout(() => {
      this.ngZone.run(() => {
        this.authService.getMe().subscribe({
          next: (me) => {
            this.me = me;
            if (me.companyId) {
              this.loadResources(me.companyId);
            } else {
              this.loading = false;
            }
          },
          error: () => {
            this.authService.logout();
            this.router.navigate(['/auth']);
          }
        });
      });
    });
  }

  loadResources(companyId: number) {
    this.resourceService.getResources(companyId).subscribe({
      next: (list) => {
        this.resources = list;
        this.filteredResources = list;
        this.allTypes = [...new Set(list.map(r => String(r.type_id)))];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSearch() {
    this.applyFilters();
  }

  selectType(type: string) {
    this.selectedType = this.selectedType === type ? '' : type;
    this.applyFilters();
  }

  applyFilters() {
    let result = this.resources;

    if (this.searchQuery.trim()) {
      const q = this.searchQuery.toLowerCase();
      result = result.filter(r =>
        r.name.toLowerCase().includes(q) ||
        r.description.toLowerCase().includes(q)
      );
    }

    if (this.selectedType) {
      result = result.filter(r => String(r.type_id) === this.selectedType);
    }

    this.filteredResources = result;
  }

  goProfile() {
    this.router.navigate(['/profile']);
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/auth']);
  }
}
