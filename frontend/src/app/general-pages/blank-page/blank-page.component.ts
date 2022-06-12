import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AngularEditorConfig } from '@kolkov/angular-editor';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ToastrService } from 'ngx-toastr';
import { DataSharingService } from 'src/app/app.datasharing.service';
import { v4 as uuidv4 } from 'uuid';

import jsPDF from 'jspdf';
import pdfMake from 'pdfmake/build/pdfmake';
import pdfFonts from 'pdfmake/build/vfs_fonts';
pdfMake.vfs = pdfFonts.pdfMake.vfs;
import htmlToPdfmake from 'html-to-pdfmake';

import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-blank-page',
  templateUrl: './blank-page.component.html',
  styleUrls: ['./blank-page.component.scss']
})
export class BlankPageComponent implements OnInit {

  @ViewChild('projectInPDF') projectInPDF: ElementRef;


  constructor(private dataSharingService: DataSharingService,
    private modalService: NgbModal,
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private toastr: ToastrService) { }

  BASE_URL_LOCAL = "http://localhost:8009"
  API_GET_SECTION_DETAILS = "gtSctnDtls"
  API_CREATE_UPDATE_SECTION_DETAILS = "crtUpdtSctnDtls"
  API_DELETE_DOCBOOK = "dltDcbk"
  API_CREATE_UPDATE_PROJECT_DETAILS = "crtUpdtPrjctDtls"
  API_UPDATE_SECTION_SEQUENCE = "updtSctnSqunce"

  htmlContent: '';

  selectedProject = ''
  selectedProjectId = ''
  selectedProjectDescription = ''
  selectedProjectIcon = ''
  selectedProjectName = ''
  selectedSectionId = ''
  selectedSectionName = ''

  CONFIRM = ''

  config: AngularEditorConfig = {
    editable: true,
    spellcheck: true,
    height: '30rem',
    minHeight: '15rem',
    placeholder: 'Enter text here...',
    translate: 'no',
    defaultParagraphSeparator: 'p',
    defaultFontName: 'Arial',
    toolbarHiddenButtons: [

    ],
    customClasses: [
      {
        name: "quote",
        class: "quote",
      },
      {
        name: 'redText',
        class: 'redText'
      },
      {
        name: "titleText",
        class: "titleText",
        tag: "h1",
      },
    ]
  };

  JSON_DATA: any = {}

  ngOnInit() {
    this.route.queryParams
      .subscribe(params => {
        // get the projects detail, populate the left sidebar
        this.getProjectSections(params.project_id, params.section_id);
      }
      );
  }

  getProjectSections(project_id, section_id) {
    // Build the URL
    var URL = this.BASE_URL_LOCAL + '/' + this.API_GET_SECTION_DETAILS
    console.log(URL)

    // create the request body
    var body = {
      "section_id": section_id,
      "project_id": project_id
    }
    // create request header
    const headers = { "Content-Type": "application/json", "api_gateway": "NOTEPAD" };
    // hit backend API
    this.http.post<any>(URL, JSON.stringify(body), { headers }).subscribe(resp => {
      if (resp === null) {
        this.showToast("error", "Error", "Something went wrong")
      } else if (resp.STATUS === 'ERROR') {
        this.showToast("error", "Error", resp.MESSAGE)
      } else if (resp.STATUS === 'SUCCESS') {
        // if project is not found show error
        if (Object.keys(resp.DATA).length === 0) {
          this.showToast("error", "Warning", "Project not found")
          this.router.navigateByUrl("home")
        }

        this.JSON_DATA = resp.DATA;
        this.selectedProject = resp.DATA
        this.selectedProjectId = resp.DATA._id
        this.selectedProjectDescription = resp.DATA.description
        this.selectedProjectIcon = resp.DATA.icon
        this.selectedProjectName = resp.DATA.name

        console.log("get data :: " + this.JSON_DATA.sections)
      }
    }, (error) => {
      this.showToast("error", "Error", "Something went wrong")
    });

  }

  editSection(section) {
    section.editSection = true;
  }

  saveSection(section) {
    section.editSection = false;

    // Build the URL
    var URL = this.BASE_URL_LOCAL + '/' + this.API_CREATE_UPDATE_SECTION_DETAILS
    console.log(URL)

    // create the request body
    var body = {
      "section_id": section._id,
      "project_id": section.project_id,
      "content": section.content,
      "name": section.name,
      "sequence": section.sequence
    }
    // create request header
    const headers = { "Content-Type": "application/json", "api_gateway": "NOTEPAD" };
    // hit backend API
    this.http.post<any>(URL, JSON.stringify(body), { headers }).subscribe(resp => {
      console.log(resp)
      if (resp === null) {
        this.showToast("error", "Error", "Something went wrong")
      } else if (resp.STATUS === 'ERROR') {
        this.showToast("error", "Error", resp.MESSAGE)
      } else if (resp.STATUS === 'SUCCESS') {
        // this.refresh()
        this.showToast("success", "Success", resp.MESSAGE)
      }
    }, (error) => {
      console.log(error)
      this.showToast("error", "Error", "Something went wrong")
    });
  }

  deleteDocBook(section_id, project_id) {
    this.CONFIRM = ''
    // Build the URL
    var URL = this.BASE_URL_LOCAL + '/' + this.API_DELETE_DOCBOOK
    console.log(URL)

    // create the request body
    var body = {};
    if (section_id != null && section_id != undefined) {
      body = {
        "section_id": section_id
      }
    } else if (project_id != null && project_id != undefined) {
      body = {
        "project_id": project_id
      }
    }

    // create request header
    const headers = { "Content-Type": "application/json", "api_gateway": "NOTEPAD" };
    // hit backend API
    this.http.post<any>(URL, JSON.stringify(body), { headers }).subscribe(resp => {
      if (resp === null) {
        this.showToast("error", "Error", "Something went wrong")
      } else if (resp.STATUS === 'ERROR') {
        this.showToast("error", "Error", resp.MESSAGE)
      } else if (resp.STATUS === 'SUCCESS') {
        this.refresh()
        this.showToast("success", "Success", resp.MESSAGE)

        // if project is deleted navigate to home
        if (project_id != null && project_id != undefined) {
          this.router.navigateByUrl("home")
          return;
        }

      }
    }, (error) => {
      this.showToast("error", "Error", "Something went wrong")
    });
    // close modal
    this.modalService.dismissAll();
  }

  openModal(modal_id, project) {
    this.selectedProject = project
    this.selectedProjectId = project._id
    this.selectedProjectDescription = project.description
    this.selectedProjectIcon = project.icon
    this.selectedProjectName = project.name

    this.modalService.open(modal_id, { size: 'lg' });

  }

  updateProject() {
    console.log("updateProject :: " + this.JSON_DATA.sections)
    // create the request body
    var body = {
      "id": this.selectedProjectId,
      "name": this.selectedProjectName,
      "description": this.selectedProjectDescription,
      "icon": this.selectedProjectIcon,
      "section": this.JSON_DATA.section
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
        this.refresh()
        // TODO refresh left sidebar 
        this.showToast("success", "Sucess", resp.MESSAGE)
      }
    }, (error) => {
      console.log(error)
      this.showToast("error", "Error", "Something went wrong")
    });
    // update the sction sequence
    this.updateSequenceForSections();

    // close modal
    this.modalService.dismissAll();
  }

  openModalForSection(addNewSectionModalContent) {
    this.modalService.open(addNewSectionModalContent, { size: 'sm' });
  }

  addNewSection(addsection_name) {
    console.log("addsection_name clicked " + addsection_name + ", id:" + this.selectedProjectId)

    // create the request body
    var body = {
      "section_id": uuidv4(),
      "project_id": this.selectedProjectId,
      "sequence": this.JSON_DATA.sections.length + 1,
      "name": addsection_name
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
        this.refresh()
        this.showToast("success", "Success", resp.MESSAGE)
      }
    }, (error) => {
      console.log(error)
      this.showToast("error", "Error", "Something went wrong")
    });
    // close modal
    this.modalService.dismissAll();
  }

  openDeleteProjectModal(deleteProjectModalContent) {
    this.modalService.open(deleteProjectModalContent, { size: 'sm' });
  }

  openDeleteSectionModal(deleteSectionModalContent, section) {
    this.selectedSectionId = section._id;
    this.selectedSectionName = section.name
    this.modalService.open(deleteSectionModalContent, { size: 'sm' });
  }

  public downloadAsPDF() {
    const doc = new jsPDF();
    const projectInPDF = this.projectInPDF.nativeElement;
    var html = htmlToPdfmake(projectInPDF.innerHTML);
    const documentDefinition = { content: html };
    pdfMake.createPdf(documentDefinition).open();
  }

  refresh() {
    // After refresh button is clicked, emit the behavior subject changes.

    // refresh the left sidebar
    this.dataSharingService.needRefresh.next(true);
    //refresh the page
    this.getProjectSections(this.selectedProjectId, null)
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

  drop(event: CdkDragDrop<string[]>) {
    moveItemInArray(this.JSON_DATA.sections, event.previousIndex, event.currentIndex);
  }

  updateSequenceForSections() {
    for (let i = 0; i < this.JSON_DATA.sections.length; i++) {
      this.JSON_DATA.sections[i].sequence = i

      // create the request body
      var body = {
        "section_id": this.JSON_DATA.sections[i]._id,
        "sequence": i
      }
      // create request header
      const headers = { "Content-Type": "application/json", "api_gateway": "NOTEPAD" };
      // hit backend API
      this.http.post<any>(`${this.BASE_URL_LOCAL}/${this.API_UPDATE_SECTION_SEQUENCE}`, JSON.stringify(body), { headers }).subscribe(resp => {
        if (resp === null) {
          this.showToast("error", "Error", "Something went wrong")
        } else if (resp.STATUS === 'ERROR') {
          this.showToast("error", "Error", resp.MESSAGE)
        } else if (resp.STATUS === 'SUCCESS') {
          // this.showToast("success", "Success", this.JSON_DATA.sections[i]+" :: "+i)
        }
      }, (error) => {
        console.log(error)
        this.showToast("error", "Error", "Something went wrong")
      });
    }
    this.refresh()
  }

  editorChange() {
    console.log("editorChanged")
  }

}


