import { Component } from '@angular/core';
import { TemplateService } from '../template.service';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute } from '@angular/router';
import { delay } from 'q';

@Component({
  selector: 'app-modify-template',
  templateUrl: './modify-template.component.html',
  styleUrls: ['./modify-template.component.scss']
})
export class ModifyTemplateComponent {

  userId: string = localStorage.getItem("userid");
  companyId: string = localStorage.getItem("companyId");
  privilegesJson: any = [];
  CTEMPLATE = false;
  RTEMPLATE = false;
  UTEMPLATE = false;
  DTEMPLATE = false;
  ATEMPLATE = false;

  templateId: any;
  sub: any;
  templateJson: any = [];
  parametersJson: any = [];
  channelsJson: any = [];
  applicationsJson: any = [];
  campaignsJson: any = [];
  originOption = 'app';
  applicationId: number;
  campaignId:number;
  formMessage = '';
  parameters: Array<string> = [];
  newParameters: Array<string> = [];
  channels_integrators: any = [];
  dateIni: string;
  dateEnd: string;
  timeIni: string;
  timeEnd: string;
  showInputCreateParameterState = false;

  constructor(private templateService: TemplateService, private toastr: ToastrService, private route: ActivatedRoute) {
    this.sub = this.route.params.subscribe(params => {
      this.templateId = +params['id'];
    });
    this.getParameters();
    this.getChannels();
    this.getApplications(Number(this.companyId));
    this.getCampaigns(Number(this.companyId));
    this.getTemplate();    
    this.getPrivileges(this.userId, Number(this.companyId));

  }

  async getPrivileges(userId: string, companyId: number) {
    this.templateService.getPrivilegesByUserAndCompany(userId, companyId).subscribe(data => {
      this.privilegesJson = data;
    });
    await delay(1000);
    this.assignPrivileges(this.privilegesJson);
  }

  assignPrivileges(privileges: Array<any>) {
    privileges.forEach((privilege) => {
      if(privilege._codePrivileges == 'CTEMPLATE'){
        this.CTEMPLATE = true;
      }
      else if(privilege._codePrivileges == 'RTEMPLATE'){
        this.RTEMPLATE = true
      }
      else if(privilege._codePrivileges == 'UTEMPLATE'){
        this.UTEMPLATE = true
      }
      else if(privilege._codePrivileges == 'DTEMPLATE'){
        this.DTEMPLATE = true
      }
      else if(privilege._codePrivileges == 'ATEMPLATE'){
        this.ATEMPLATE = true
      }
    })
  }

  getParameters() {
    this.templateService.getParameters(Number(this.companyId)).subscribe(data => {
      this.parametersJson = data;
    });
  }

  getChannels() {
    this.templateService.getChannels().subscribe(data => {
      this.channelsJson = data;
    });
  }

  getApplications(company: number) {
    this.templateService.getApplicationsByCompany(company).subscribe(data => {
      this.applicationsJson = data;
    });
  }

  getCampaigns(company: number) {
    this.templateService.getCampaigns(company).subscribe(data => {
      this.campaignsJson = data;
    });
  }

 getTemplate() {
    this.templateService.getTemplate(this.templateId).subscribe(data => {
      this.templateJson = data;
      console.log(this.templateJson);
      this.formMessage = this.templateJson.message.message;
      this.dateIni = this.templateJson.planning.startDate.substring(0,10);
      this.dateEnd = this.templateJson.planning.endDate.substring(0,10);
      this.timeIni = this.templateJson.planning.startTime;
      this.timeEnd = this.templateJson.planning.endTime;
      this.applicationId = this.templateJson.application._idApplication;
      this.assignParameter(this.parameters, this.templateJson.message.parameterArrayList);
      this.assignChannelsIntegrators(this.channels_integrators, this.templateJson.channels);
      console.log(data);
    });
    
    
  }

  assignParameter(place: Array<any>, data: Array<any>) {
    data.forEach((value) => {
      place.push(value.name);
    });
  }

  assignChannelsIntegrators(place: Array<any>, data: Array<any>) {
    data.forEach((channel) => {
      channel._integrators.forEach((integrator) => {
        place.push(
          { channel, integrator }
        );
      });
    });
  }

  addParameter(message: string, parameterName: string) {
    const myFormMessage = document.getElementById('formMessage');
    const pointer = (myFormMessage as HTMLTextAreaElement).selectionStart;
    const startMessage = message.slice(0, pointer);
    const endMessage = message.slice(pointer, message.length);
    this.parameters.push(parameterName);
    this.formMessage = startMessage + ' [.$' + parameterName + '$.] ' + endMessage;
  }

  addNewParameter(message: string, parameterName: string) {
    parameterName = parameterName.trim();
    if ((parameterName.valueOf() !== '')) {
      parameterName = parameterName.toLowerCase();
      parameterName = parameterName.charAt(0).toUpperCase() + parameterName.slice(1, parameterName.length);
      if (!this.parameters.find(x => x === parameterName)) {
        this.addParameter(message, parameterName);
        if (!this.newParameters.find(x => x == parameterName)) {
          this.newParameters.push(parameterName);
        } else {
          this.toastr.warning('El parametro ya esta registrado', 'Aviso',
            {
              timeOut: 2800,
              progressBar: true
            });
        }
      }
    } else {
      this.toastr.warning('No a escrito ningun parametro', 'Aviso',
        {
          timeOut: 2800,
          progressBar: true
        });
    }
  }

  addIntegrator(channel: any, integratorId: number) {
    if (!this.channels_integrators.find(x => x.channel._id == channel._id)) {
      if (!this.channels_integrators.find(x => x.integrator._id == integratorId)) {
        const integrator = channel._integrators.find(x => x._id == integratorId);
        this.channels_integrators.push(
          { channel, integrator }
        );
      }
    }
  }

  showInputCreateParameter() {
    this.showInputCreateParameterState = true;
  }

  deleteParameter(message: string, parameterName: string) {
    var text = '[.$'+parameterName+'$.]';
    this.formMessage=message.replace(text,'');
    this.parameters.splice(this.parameters.indexOf(parameterName), 1);
    if (this.newParameters.find(x => x == parameterName)) {
      this.newParameters.splice(this.newParameters.indexOf(parameterName), 1);
    }
  }

  deleteChannel_Integrator(channel_integrator) {
    this.channels_integrators.splice(this.channels_integrators.indexOf(channel_integrator), 1);
  }

  updateTemplate() {
      const planning: any = [];
      if (this.originOption !== 'app'){
          this.applicationId = 0;
      }
      if (this.dateIni < this.dateEnd) {
          planning.push(this.dateIni, this.dateEnd, this.timeIni, this.timeEnd);
          this.formMessage = this.formMessage.trim();
          if (this.formMessage != '') {
              if ((this.formMessage !== undefined) && (this.formMessage.length > 5)) {
                  if (this.channels_integrators[0]) {
                      this.templateService.updateTemplate(this.templateId, this.formMessage, this.parameters, this.newParameters, Number(this.companyId), this.channels_integrators, this.campaignId ,this.applicationId, planning);
                  } else {
                      this.toastr.error('Falta llenar un campo', 'Error',
                          {
                              timeOut: 2800,
                              progressBar: true
                          });
                  }
              } else {
                  this.toastr.error('Tal vez quiera escribir un mensaje mas largo', 'Error',
                      {
                          timeOut: 2800,
                          progressBar: true
                      });
              }
          } else {
              this.toastr.warning('No puede crear un template sin mensaje!', 'Aviso',
                  {
                      timeOut: 2800,
                      progressBar: true
                  });
          }
      } else {
          this.toastr.warning('La fecha inicial no puede ser superior a la final', 'Aviso',
              {
                  timeOut: 2800,
                  progressBar: true
              });
      }
  }
}
