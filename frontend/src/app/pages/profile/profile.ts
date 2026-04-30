import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile',
  standalone: true,
  templateUrl: './profile.html',
})
export class ProfileComponent {
  constructor(private router: Router) {}

  goAuth() {
    this.router.navigate(['/auth']);
  }
}
