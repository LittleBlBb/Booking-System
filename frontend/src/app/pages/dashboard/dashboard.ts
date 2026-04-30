import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.html',
})
export class DashboardComponent {
  constructor(private router: Router) {}

  goProfile() {
    this.router.navigate(['/profile']);
  }
}
