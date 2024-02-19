import { Component, OnInit } from '@angular/core';
import { SettingsDto, SettingService } from '../../generated-source/api';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-settings',
  templateUrl: './settings.component.html',
  styleUrls: ['./settings.component.scss'],
})
export class SettingsComponent implements OnInit {
  settings$: Observable<SettingsDto>;

  constructor(private settingService: SettingService) {}

  ngOnInit(): void {
    this.settings$ = this.settingService.getSettings();
  }
}
