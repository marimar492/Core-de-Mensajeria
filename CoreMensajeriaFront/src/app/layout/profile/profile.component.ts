import { Component, OnInit, Input } from '@angular/core';
import { routerTransition } from '../../router.animations';
import { Router, ActivatedRoute } from '@angular/router';
import { Users } from './models/users';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Http } from '@angular/http';
import { EditProfile } from './edit_profile.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
  animations: [routerTransition()]
})
export class ProfileComponent implements OnInit {

  Users : Array<Users>;
  id : number;
  private sub: any;

  constructor(private route: ActivatedRoute, public router: Router, private http: Http, public rest: EditProfile, private toastr: ToastrService){
    
  }

  ngOnInit() {
    this.sub = this.route.params.subscribe(params => {
      this.id = +params['id'];
    if( this.id ){
      this.http.get('http://localhost:8080/CoreMensajeria_war_exploded/profile/user/'+ this.id ).subscribe(resp=>{
        this.Users = [resp.json()]; 
      });
    }
    else{
      this.http.get('http://localhost:8080/CoreMensajeria_war_exploded/profile/user/'+ localStorage.userid ).subscribe(resp=>{
        this.Users = [resp.json()]; 
      });
    }
   });
  }

  edit() {
    this.toastr.info("Espere un momento",'Intentando acceder',{
      progressBar: true
    });
    this.rest.editProfile(this.Users[0]).subscribe((result) => {
      this.toastr.success('Editado con éxito');
      this.router.navigate(['/createuser']);
    }, (err) => {
      // console.log(err);
      if (err.status == 0) this.toastr.error('Problema de conexión'); //Aqui poner mensaje de la excepcion
      else this.toastr.error(err.error._error);
    });
  }

  handleEdit() {
    console.log(this.Users[0]);
    if (this.Users[0]._addressUser.length >0 && this.Users[0]._emailUser.length > 0 && this.Users[0]._phoneUser )
      this.edit();
    else
      this.toastr.error('Debe llenar el formulario');
  }

}
