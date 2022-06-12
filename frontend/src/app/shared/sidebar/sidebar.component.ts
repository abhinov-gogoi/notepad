import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { DataSharingService } from 'src/app/app.datasharing.service';
import { v4 as uuidv4 } from 'uuid';
import { timeout } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {
  BASE_URL_LOCAL = "http://localhost:8009"
  API_CREATE_UPDATE_PROJECT_DETAILS = "crtUpdtPrjctDtls"
  API_GET_PROJECT_DETAILS = "gtPrjctDtls"
  API_CREATE_UPDATE_SECTION_DETAILS = "crtUpdtSctnDtls"

  public uiBasicCollapsed = false;
  public samplePagesCollapsed = false;

  addproject_description = ''
  addproject_name = ''

  selected_project_id = '';

  needRefresh: boolean = false;

  public projects: any = []

  constructor(private dataSharingService: DataSharingService,
    private modalService: NgbModal,
    private http: HttpClient,
    private router: Router,
    private toastr: ToastrService) {

    // Subscribe here, this will automatically update 
    // "needRefresh" whenever a change to the subject is made.
    this.dataSharingService.needRefresh.subscribe(value => {
      this.getProjectDetails()
    });
  }

  ngOnInit() {
    const body = document.querySelector('body');

    // add class 'hover-open' to sidebar navitem while hover in sidebar-icon-only menu
    document.querySelectorAll('.sidebar .nav-item').forEach(function (el) {
      el.addEventListener('mouseover', function () {
        if (body.classList.contains('sidebar-icon-only')) {
          el.classList.add('hover-open');
        }
      });
      el.addEventListener('mouseout', function () {
        if (body.classList.contains('sidebar-icon-only')) {
          el.classList.remove('hover-open');
        }
      });
    });

    // get the projects detail, populate the left sidebar
    this.getProjectDetails();


  }

  getProjectDetails() {
    // Build the URL
    var URL = this.BASE_URL_LOCAL + '/' + this.API_GET_PROJECT_DETAILS;
    console.log(URL)
    // create request header
    const headers = { "Content-Type": "application/json", "api_gateway": "NOTEPAD" };

    // hit backend API
    this.http.get<any>(URL, { "headers": headers })
      .pipe(timeout(2000))
      .subscribe(resp => {
        if (resp === null || resp.STATUS !== "SUCCESS") {
          this.showToast("error", "Error", "Something went wrong")
        }
        else {
          this.projects = resp.DATA.reverse()
        }
      }, (error) => {
        console.log(error)
        this.showToast("error", "Error", "Could not fetch project details")
      });
  }

  openNewProjectModal(newProjectModalContent) {
    this.modalService.open(newProjectModalContent, { size: 'lg' });
  }

  addProject(projectName, projectDescription, projectIcon) {
    // create the request body
    var uuid: string = uuidv4();

    var body = {
      "id": uuid,
      "name": projectName,
      "description": projectDescription,
      "icon": projectIcon
    }
    // create request header
    const headers = { "Content-Type": "application/json", "api_gateway": "NOTEPAD" };
    // hit backend API
    this.http.post<any>(`${this.BASE_URL_LOCAL}/${this.API_CREATE_UPDATE_PROJECT_DETAILS}`, JSON.stringify(body), { headers }).subscribe(resp => {
      if (resp === null) {
        this.showToast("error", "Error", "Something went wrong")
      } else if (resp.STATUS === 'ERROR') {
        this.showToast("error", "Error", resp.MESSAGE)
      } else if (resp.STATUS === 'SUCCESS') {
        this.getProjectDetails()
        this.showToast("success", "Success", "New Project Added - " + projectName)
        this.router.navigateByUrl("general-pages/blank-page?project_id=" + uuid);
        return;
      }
    }, (error) => {
      console.log(error)
      this.showToast("error", "Error", "Something went wrong")
    });
    // close modal
    this.modalService.dismissAll();
  }

  openModalForSection(addNewSectionModalContent, project_id) {
    console.log("openModalForSection clicked " + project_id)
    this.selected_project_id = project_id
    this.modalService.open(addNewSectionModalContent, { size: 'sm' });
  }

  refresh() {
    this.router.navigateByUrl("general-pages/blank-page?project_id=" + this.selected_project_id)
  }

  gotoProjectPageDescription(project) {
    this.selected_project_id = project._id
    if (!project.collapsed) {
      project.collapsed = !project.collapsed
    } else {
      project.collapsed = !project.collapsed
      this.router.navigateByUrl("general-pages/blank-page?project_id=" + project._id)
    }
  }

  gotoProjectPage(project_id, section_id) {
    this.selected_project_id = project_id
    this.router.navigateByUrl("general-pages/blank-page?project_id=" + project_id + "&section_id=" + section_id)
  }

  addNewSection(addsection_name) {
    console.log("addsection_name clicked " + addsection_name + ", id:" + this.selected_project_id)

    // create the request body
    var body = {
      "section_id": uuidv4(),
      "project_id": this.selected_project_id.trim(),
      "name": addsection_name.trim()
    }
    // create request header
    const headers = { "Content-Type": "application/json", "api_gateway": "NOTEPAD" };
    // hit backend API
    this.http.post<any>(`${this.BASE_URL_LOCAL}/${this.API_CREATE_UPDATE_SECTION_DETAILS}`, JSON.stringify(body), { headers }).subscribe(resp => {
      if (resp === null) {
        this.showToast("error", "Error", "Something went wrong")
      } else if (resp.STATUS === 'ERROR') {
        this.showToast("error", "Error", resp.MESSAGE)
      } else if (resp.STATUS === 'SUCCESS') {
        this.getProjectDetails()
        this.showToast("error", "Success", "New section added - " + addsection_name)
      }
    }, (error) => {
      console.log(error)
      this.showToast("error", "Error", "Something went wrong")
    });
    // close modal
    this.modalService.dismissAll();
  }

  showToast(type: string, heading: string, content: string) {
    // don't show toaster heading - its already implied by the toaster colour
    heading = ''
    if (type === 'success') {
      this.toastr.success(content, heading);
      return;
    } else if (type === 'error') {
      this.toastr.error(content, heading);
      return;
    } else if (type === 'info') {
      this.toastr.info(content, heading);
      return;
    } else if (type === 'warning') {
      this.toastr.warning(content, heading);
      return;
    } else {
      this.toastr.info(content, heading);
    }
    return;
  }


}
