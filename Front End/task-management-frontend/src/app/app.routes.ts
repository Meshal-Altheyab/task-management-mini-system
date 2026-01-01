import { Routes } from '@angular/router';

import { authGuard } from './core/guards/auth.guard';

import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { TasksMyComponent } from './pages/tasks-my/tasks-my.component';
import { TasksAllComponent } from './pages/tasks-all/tasks-all.component';
import { CreateTaskComponent } from './pages/create-task/create-task.component';
import { EditTaskComponent } from './pages/edit-task/edit-task.component';
import { TaskDetailsComponent } from './pages/task-details/task-details.component';
import { ReportsComponent } from './pages/reports/reports.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },

  { path: 'my-tasks', component: TasksMyComponent, canActivate: [authGuard] },
  { path: 'create-task', component: CreateTaskComponent, canActivate: [authGuard] },
  { path: 'edit-task/:id', component: EditTaskComponent, canActivate: [authGuard] },
  { path: 'task/:id', component: TaskDetailsComponent, canActivate: [authGuard] },
  { path: 'all-tasks', component: TasksAllComponent, canActivate: [authGuard] },
  { path: 'reports', component: ReportsComponent, canActivate: [authGuard] },

  { path: '', redirectTo: 'my-tasks', pathMatch: 'full' },
  { path: '**', redirectTo: 'my-tasks' }
];
