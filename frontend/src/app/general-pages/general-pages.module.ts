import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BlankPageComponent } from './blank-page/blank-page.component';
import { Routes, RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { AngularEditorModule } from '@kolkov/angular-editor';
import { ToastrModule } from 'ngx-toastr';
import { DragDropModule } from '@angular/cdk/drag-drop';

const routes : Routes = [
  { path: 'blank-page', component: BlankPageComponent }
]

@NgModule({
  declarations: [BlankPageComponent],
  imports: [
    CommonModule,
    RouterModule.forChild(routes),
    NgbModule, 
    FormsModule,
    AngularEditorModule,
    ToastrModule.forRoot(),
    DragDropModule
  ]
})
export class GeneralPagesModule { }
