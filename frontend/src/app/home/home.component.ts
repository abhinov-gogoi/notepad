import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit {

  toggleProBanner(event) {
    console.log("123");
    event.preventDefault();
    document.querySelector('body').classList.toggle('removeProbanner');
  }

  constructor(private toastr: ToastrService) { }

  ngOnInit() {
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
