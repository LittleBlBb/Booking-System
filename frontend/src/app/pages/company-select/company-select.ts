import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-company-select',
  standalone: true,
  templateUrl: './company-select.html',
})
export class CompanySelectComponent {
  constructor(private router: Router) {}

  goDashboard() {
    this.router.navigate(['/dashboard']);
  }
}
