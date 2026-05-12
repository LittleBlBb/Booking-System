import { Component, OnInit, ChangeDetectorRef, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService, Me } from '../../core/services/auth.service';
import { AdminService, CompanyUser, ResourceType, JoinRequest } from '../../core/services/admin.service';
import { ResourceService, Resource } from '../../core/services/resource.service';
import { CompanySettings } from '../../core/services/booking.service';
import { RESOURCE_ICONS, IconOption, getIconById } from '../../core/utils/icons';

type Tab = 'settings' | 'resources' | 'users';
type BannerType = 'success' | 'error' | null;

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './admin.html',
})
export class AdminComponent implements OnInit {

  me: Me | null = null;
  activeTab: Tab = 'settings';
  loading = true;

  // --- Settings ---
  settings: CompanySettings | null = null;
  settingsExist = false;
  settingsForm = { maxBookingsPerUser: 0, maxBookingDurationMinutes: 0, workStart: '08:00', workEnd: '20:00' };
  settingsSaving = false;

  // --- Users ---
  users: CompanyUser[] = [];
  usersLoading = false;
  roleOptions = ['USER', 'ADMIN'];
  showRemoveUserModal = false;
  removeUserTarget: CompanyUser | null = null;
  showViewUserModal = false;
  viewUserTarget: CompanyUser | null = null;

  // --- Join requests ---
  joinRequests: JoinRequest[] = [];
  joinRequestsLoading = false;

  // --- Resource types ---
  resourceTypes: ResourceType[] = [];
  newTypeName = '';
  editTypeId: number | null = null;
  editTypeName = '';
  editTypeIconId: number | null = null;
  newTypeIconId: number | null = null;

  // --- Icon picker ---
  showIconPicker = false;
  iconPickerTarget: { typeId: number | null; isNew: boolean; isEdit: boolean } | null = null;
  allIcons: IconOption[] = RESOURCE_ICONS;

  // --- Resources ---
  resources: Resource[] = [];
  resourcesLoading = false;
  showResourceModal = false;
  editingResource: Resource | null = null;
  resourceForm = { name: '', description: '', resourceTypeId: 0, quantity: 1 };
  resourceFormErrors: any = {};
  resourceSaving = false;
  showDeleteResourceModal = false;
  deleteResourceTarget: Resource | null = null;
  showViewResourceModal = false;
  viewResourceTarget: Resource | null = null;

  // --- Delete company ---
  showDeleteCompanyModal = false;
  deletingCompany = false;

  banner: string | null = null;
  bannerKind: BannerType = null;
  private bannerTimer: any = null;

  constructor(
    private authService: AuthService,
    private adminService: AdminService,
    private resourceService: ResourceService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    setTimeout(() => {
      this.authService.getMe().subscribe({
        next: (me) => {
          this.me = me;
          this.loading = false;
          this.loadSettings();
          this.loadResourceTypes();
          this.cdr.detectChanges();
        },
        error: () => { this.authService.logout(); this.router.navigate(['/auth']); }
      });
    });
  }

  setTab(tab: Tab) {
    this.activeTab = tab;
    if (tab === 'users') {
      if (this.users.length === 0) this.loadUsers();
      this.loadJoinRequests();
    }
    if (tab === 'resources' && this.resources.length === 0) this.loadResources();
  }

  // ---------------- ICON HELPERS ----------------

  getIconById(id: number | null | undefined): string {
    return getIconById(id);
  }

  getTypeIcon(typeId: number): string {
    const type = this.resourceTypes.find(t => t.id === typeId);
    return getIconById(type?.iconId);
  }

  openIconPicker(typeId: number | null, isNew: boolean, isEdit: boolean) {
    this.iconPickerTarget = { typeId, isNew, isEdit };
    this.showIconPicker = true;
  }

  selectIcon(icon: IconOption) {
    if (!this.iconPickerTarget) return;
    const { typeId, isNew, isEdit } = this.iconPickerTarget;

    if (isNew) {
      this.newTypeIconId = icon.id;
    } else if (isEdit) {
      this.editTypeIconId = icon.id;
    } else if (typeId !== null) {
      // Быстрая смена иконки без входа в режим редактирования
      const type = this.resourceTypes.find(t => t.id === typeId);
      if (type) {
        this.adminService.updateResourceType(type.id, type.name, icon.id).subscribe({
          next: (updated) => {
            const idx = this.resourceTypes.findIndex(t => t.id === typeId);
            if (idx !== -1) this.resourceTypes[idx] = updated;
            this.cdr.detectChanges();
            this.setBanner('Иконка обновлена', 'success');
          },
          error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
        });
      }
    }

    this.showIconPicker = false;
    this.iconPickerTarget = null;
  }

  closeIconPicker() {
    this.showIconPicker = false;
    this.iconPickerTarget = null;
  }

  companyAvatar(name: string | null | undefined): string {
    if (!name) return '?';
    const words = name.trim().split(/\s+/);
    if (words.length >= 2) return (words[0][0] + words[words.length - 1][0]).toUpperCase();
    return name.slice(0, 2).toUpperCase();
  }

  // ---------------- SETTINGS ----------------

  loadSettings() {
    this.adminService.getSettings().subscribe({
      next: (s) => {
        this.settings = s;
        this.settingsExist = !!s.companyId;
        this.settingsForm = {
          maxBookingsPerUser: s.maxBookingsPerUser || 0,
          maxBookingDurationMinutes: s.maxBookingDurationMinutes || 0,
          workStart: s.workStart?.slice(0, 5) || '08:00',
          workEnd: s.workEnd?.slice(0, 5) || '20:00'
        };
        this.cdr.detectChanges();
      }
    });
  }

  saveSettings() {
    this.settingsSaving = true;
    const body = {
      maxBookingsPerUser: this.settingsForm.maxBookingsPerUser,
      maxBookingDurationMinutes: this.settingsForm.maxBookingDurationMinutes,
      workStart: this.settingsForm.workStart + ':00',
      workEnd: this.settingsForm.workEnd + ':00'
    };
    const req = this.settingsExist ? this.adminService.updateSettings(body) : this.adminService.createSettings(body);
    req.subscribe({
      next: (s) => { this.settings = s; this.settingsExist = true; this.settingsSaving = false; this.setBanner('Настройки сохранены', 'success'); },
      error: (err: HttpErrorResponse) => { this.settingsSaving = false; this.setBanner(err.error?.message || 'Ошибка при сохранении', 'error'); }
    });
  }

  // ---------------- USERS ----------------

  loadUsers() {
    if (!this.me?.companyId) return;
    this.usersLoading = true;
    this.adminService.getCompanyUsers(this.me.companyId).subscribe({
      next: (u) => { this.users = u; this.usersLoading = false; this.cdr.detectChanges(); },
      error: () => { this.usersLoading = false; this.cdr.detectChanges(); }
    });
  }

  changeRole(user: CompanyUser, role: string) {
    this.adminService.updateUserRole(user.id, role).subscribe({
      next: (updated) => {
        const idx = this.users.findIndex(u => u.id === user.id);
        if (idx !== -1) this.users[idx] = updated;
        this.setBanner(`Роль ${updated.username} изменена на ${updated.role}`, 'success');
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  openRemoveUserModal(user: CompanyUser) { this.removeUserTarget = user; this.showRemoveUserModal = true; }
  closeRemoveUserModal() { this.showRemoveUserModal = false; this.removeUserTarget = null; }

  confirmRemoveUser() {
    if (!this.removeUserTarget) return;
    const id = this.removeUserTarget.id;
    this.closeRemoveUserModal();
    this.closeViewUserModal();
    this.adminService.removeUserFromCompany(id).subscribe({
      next: () => { this.users = this.users.filter(u => u.id !== id); this.setBanner('Пользователь удалён из компании', 'success'); this.cdr.detectChanges(); },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  openViewUserModal(user: CompanyUser) { this.viewUserTarget = user; this.showViewUserModal = true; }
  closeViewUserModal() { this.showViewUserModal = false; this.viewUserTarget = null; }

  isSelf(user: CompanyUser): boolean { return user.id === this.me?.id; }

  private normalizeRole(role: string | undefined | null): string {
    if (!role) return '';
    return role.replace(/^ROLE_/, '');
  }

  canManageUser(user: CompanyUser): boolean {
    if (this.isSelf(user)) return false;
    const roleRank: Record<string, number> = { USER: 1, ADMIN: 2, OWNER: 3 };
    const myRank = roleRank[this.normalizeRole(this.me?.role)] ?? 0;
    const theirRank = roleRank[this.normalizeRole(user.role)] ?? 0;
    return myRank > theirRank;
  }

  get isOwner(): boolean { return this.normalizeRole(this.me?.role) === 'OWNER'; }

  // ---------------- JOIN REQUESTS ----------------

  loadJoinRequests() {
    this.joinRequestsLoading = true;
    this.adminService.getJoinRequests().subscribe({
      next: (r) => { this.joinRequests = r; this.joinRequestsLoading = false; this.cdr.detectChanges(); },
      error: () => { this.joinRequestsLoading = false; this.cdr.detectChanges(); }
    });
  }

  approveRequest(request: JoinRequest) {
    this.adminService.approveJoinRequest(request.id).subscribe({
      next: () => { this.joinRequests = this.joinRequests.filter(r => r.id !== request.id); this.users = []; this.loadUsers(); this.setBanner('Запрос одобрен', 'success'); },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  rejectRequest(request: JoinRequest) {
    this.adminService.rejectJoinRequest(request.id).subscribe({
      next: () => { this.joinRequests = this.joinRequests.filter(r => r.id !== request.id); this.setBanner('Запрос отклонён', 'success'); this.cdr.detectChanges(); },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  formatDate(dateStr: string): string {
    const d = new Date(dateStr);
    return d.toLocaleDateString('ru-RU', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' });
  }

  // ---------------- RESOURCE TYPES ----------------

  loadResourceTypes() {
    this.adminService.getResourceTypes().subscribe({
      next: (t) => { this.resourceTypes = t; this.cdr.detectChanges(); }
    });
  }

  addResourceType() {
    if (!this.newTypeName.trim() || !this.me?.companyId) return;
    this.adminService.createResourceType(this.newTypeName.trim(), this.me.companyId).subscribe({
      next: (t) => {
        if (this.newTypeIconId) {
          this.adminService.updateResourceType(t.id, t.name, this.newTypeIconId).subscribe({
            next: (updated) => { this.resourceTypes.push(updated); this.newTypeName = ''; this.newTypeIconId = null; this.cdr.detectChanges(); this.setBanner('Тип добавлен', 'success'); }
          });
        } else {
          this.resourceTypes.push(t);
          this.newTypeName = '';
          this.cdr.detectChanges();
          this.setBanner('Тип добавлен', 'success');
        }
      },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  startEditType(type: ResourceType) {
    this.editTypeId = type.id;
    this.editTypeName = type.name;
    this.editTypeIconId = type.iconId;
  }

  cancelEditType() { this.editTypeId = null; this.editTypeName = ''; this.editTypeIconId = null; }

  saveEditType(type: ResourceType) {
    if (!this.editTypeName.trim()) return;
    this.adminService.updateResourceType(type.id, this.editTypeName.trim(), this.editTypeIconId).subscribe({
      next: (updated) => {
        const idx = this.resourceTypes.findIndex(t => t.id === type.id);
        if (idx !== -1) this.resourceTypes[idx] = updated;
        this.editTypeId = null;
        this.setBanner('Тип обновлён', 'success');
        this.cdr.detectChanges();
      },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  deleteResourceType(type: ResourceType) {
    this.adminService.deleteResourceType(type.id).subscribe({
      next: () => { this.resourceTypes = this.resourceTypes.filter(t => t.id !== type.id); this.setBanner('Тип удалён', 'success'); this.cdr.detectChanges(); },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  // ---------------- RESOURCES ----------------

  loadResources() {
    if (!this.me?.companyId) return;
    this.resourcesLoading = true;
    this.resourceService.getResources(this.me.companyId).subscribe({
      next: (r) => { this.resources = r; this.resourcesLoading = false; this.cdr.detectChanges(); },
      error: () => { this.resourcesLoading = false; this.cdr.detectChanges(); }
    });
  }

  openCreateResource() {
    this.editingResource = null;
    this.resourceForm = { name: '', description: '', resourceTypeId: this.resourceTypes[0]?.id || 0, quantity: 1 };
    this.resourceFormErrors = {};
    this.showResourceModal = true;
  }

  openEditResource(resource: Resource) {
    this.editingResource = resource;
    this.resourceForm = { name: resource.name, description: resource.description, resourceTypeId: resource.type_id, quantity: resource.quantity };
    this.resourceFormErrors = {};
    this.showResourceModal = true;
    this.showViewResourceModal = false;
  }

  closeResourceModal() { this.showResourceModal = false; this.editingResource = null; }

  saveResource() {
    this.resourceFormErrors = {};
    if (!this.resourceForm.name.trim()) this.resourceFormErrors.name = 'Введите название';
    if (!this.resourceForm.resourceTypeId) this.resourceFormErrors.type = 'Выберите тип';
    if (this.resourceForm.quantity < 1) this.resourceFormErrors.quantity = 'Минимум 1';
    if (Object.keys(this.resourceFormErrors).length > 0) return;

    this.resourceSaving = true;
    const companyId = this.me!.companyId!;

    if (this.editingResource) {
      this.adminService.editResource({ id: this.editingResource.id, ...this.resourceForm, companyId }).subscribe({
        next: (updated) => {
          const idx = this.resources.findIndex(r => r.id === this.editingResource!.id);
          if (idx !== -1) this.resources[idx] = updated;
          this.resourceSaving = false;
          this.closeResourceModal();
          this.setBanner('Ресурс обновлён', 'success');
          this.cdr.detectChanges();
        },
        error: (err: HttpErrorResponse) => { this.resourceSaving = false; this.setBanner(err.error?.message || 'Ошибка', 'error'); }
      });
    } else {
      this.adminService.addResource({ ...this.resourceForm, companyId }).subscribe({
        next: (created) => {
          this.resources.push(created);
          this.resourceSaving = false;
          this.closeResourceModal();
          this.setBanner('Ресурс создан', 'success');
          this.cdr.detectChanges();
        },
        error: (err: HttpErrorResponse) => { this.resourceSaving = false; this.setBanner(err.error?.message || 'Ошибка', 'error'); }
      });
    }
  }

  openDeleteResource(resource: Resource) { this.deleteResourceTarget = resource; this.showDeleteResourceModal = true; }
  closeDeleteResource() { this.showDeleteResourceModal = false; this.deleteResourceTarget = null; }

  confirmDeleteResource() {
    if (!this.deleteResourceTarget) return;
    const id = this.deleteResourceTarget.id;
    this.closeDeleteResource();
    this.closeViewResourceModal();
    this.adminService.deleteResource(id).subscribe({
      next: () => { this.resources = this.resources.filter(r => r.id !== id); this.setBanner('Ресурс удалён', 'success'); this.cdr.detectChanges(); },
      error: (err: HttpErrorResponse) => this.setBanner(err.error?.message || 'Ошибка', 'error')
    });
  }

  openViewResourceModal(resource: Resource) { this.viewResourceTarget = resource; this.showViewResourceModal = true; }
  closeViewResourceModal() { this.showViewResourceModal = false; this.viewResourceTarget = null; }

  openDeleteFromView(resource: Resource) {
    this.deleteResourceTarget = resource;
    this.showDeleteResourceModal = true;
  }

  getTypeName(typeId: number): string {
    return this.resourceTypes.find(t => t.id === typeId)?.name || `Тип #${typeId}`;
  }

  // ---------------- DELETE COMPANY ----------------

  openDeleteCompanyModal() { this.showDeleteCompanyModal = true; }
  closeDeleteCompanyModal() { this.showDeleteCompanyModal = false; }

  confirmDeleteCompany() {
    if (!this.me?.companyId) return;
    this.deletingCompany = true;
    this.adminService.deleteCompany(this.me.companyId).subscribe({
      next: () => { this.deletingCompany = false; this.authService.logout(); this.router.navigate(['/auth']); },
      error: (err: HttpErrorResponse) => { this.deletingCompany = false; this.closeDeleteCompanyModal(); this.setBanner(err.error?.message || 'Ошибка при удалении компании', 'error'); }
    });
  }

  // ---------------- NAV ----------------

  goDashboard() { this.router.navigate(['/dashboard']); }
  goMyBookings() { this.router.navigate(['/my-bookings']); }
  goProfile() { this.router.navigate(['/profile']); }
  logout() { this.authService.logout(); this.router.navigate(['/auth']); }

  // ---------------- BANNER ----------------

  setBanner(message: string, type: BannerType) {
    if (this.bannerTimer) clearTimeout(this.bannerTimer);
    this.banner = message;
    this.bannerKind = type;
    this.cdr.detectChanges();
    this.bannerTimer = setTimeout(() => { this.banner = null; this.bannerKind = null; this.cdr.detectChanges(); this.bannerTimer = null; }, 5000);
  }

  closeBanner() {
    if (this.bannerTimer) clearTimeout(this.bannerTimer);
    this.banner = null; this.bannerKind = null;
  }
}
