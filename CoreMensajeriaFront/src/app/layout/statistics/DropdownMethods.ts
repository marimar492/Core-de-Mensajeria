import { StatisticsServiceService } from "./statistics-service.service";
import { ToastrService } from "ngx-toastr";

enum EntityType {
    company = 1,
    campaign,
    channel,
    integrator
}

export class DropdownMethods {
    constructor(
        public statisticsService: StatisticsServiceService,
        public toastr: ToastrService
    ) {
        this.userId = localStorage.getItem("userid");

        this.setupCompaniesDropdownSettings();
        this.setupCampaignsDropdownSettings();
        this.setupChannelsDropdownSettings();
        this.setupIntegratorsDropdownSettings();
        this.setupYearsDropdownSettings();
        this.setupMonthsDropdownSettings();
        this.setupDaysDropdownSettings();
        this.setupWeeksDropdownSettings();
        this.setupDaysOfWeekDropdownSettings();
        this.setupQuartersOfYearDropdownSettings();
        this.setupDaysOfYearDropdownSettings();
        this.setupHoursDropdownSettings();
        this.setupMinutesDropdownSettings();
        this.setupSecondsDropdownSettings();

        console.log(this.integratorsDropdown);
    }

    userId: String;

    /* ==========================
           Filter dropdowns
    ============================= */
    companiesDropdown = [];
    companiesDropdownSettings = {};
    selectedCompaniesIds: Number[] = [];
    selectedCompanies = [];

    campaignsDropdown = [];
    campaignsDropdownSettings = {};
    selectedCampaignsIds = [];
    selectedCampaigns = [];

    channelsDropdown = [];
    channelsDropdownSettings = {};
    selectedChannelsIds = [];
    selectedChannels = [];

    integratorsDropdown = [];
    integratorsDropdownSettings = {};
    selectedIntegratorsIds = [];
    selectedIntegrators = [];

    yearsDropdown = [];
    yearsDropdownSettings = {};
    selectedYearsIds = [];
    selectedYears = [];

    monthsDropdown = [];
    monthsDropdownSettings = {};
    selectedMonthsIds = [];
    selectedMonths = [];

    daysDropdown = [];
    daysDropdownSettings = {};
    selectedDaysIds = [];
    selectedDays = [];

    weeksDropdown = [];
    weeksDropdownSettings = {};
    selectedWeeksIds = [];
    selectedWeeks = [];

    daysOfWeekDropdown = [];
    daysOfWeekDropdownSettings = {};
    selectedDaysOfWeekIds = [];
    selectedDaysOfWeek = [];

    quartersOfYearDropdown = [];
    quartersOfYearDropdownSettings = {};
    selectedQuartersOfYearIds = [];
    selectedQuartersOfYear = [];

    daysOfYearDropdown = [];
    daysOfYearDropdownSettings = {};
    selectedDaysOfYearIds = [];
    selectedDaysOfYear = [];

    hoursDropdown = [];
    hoursDropdownSettings = {};
    selectedHoursIds = [];
    selectedHours = [];

    minutesDropdown = [];
    minutesDropdownSettings = {};
    selectedMinutesIds = [];
    selectedMinutes = [];

    secondsDropdown = [];
    secondsDropdownSettings = {};
    selectedSecondsIds = [];
    selectedSeconds = [];

    protected setupCompaniesDropdownSettings() {
        this.companiesDropdownSettings = this.setupDropdownSettings(
            "company_id",
            "company_name"
        );
    }

    protected setupCampaignsDropdownSettings() {
        this.campaignsDropdownSettings = this.setupDropdownSettings(
            "campaign_id",
            "campaign_name"
        );
    }

    protected setupChannelsDropdownSettings() {
        this.channelsDropdownSettings = this.setupDropdownSettings(
            "channel_id",
            "channel_name"
        );
    }

    protected setupIntegratorsDropdownSettings() {
        this.integratorsDropdownSettings = this.setupDropdownSettings(
            "integrator_id",
            "integrator_name"
        );
    }

    protected setupYearsDropdownSettings() {
        this.yearsDropdownSettings = this.setupDropdownSettings(
            "year_id",
            "year_name"
        );
    }

    protected setupMonthsDropdownSettings() {
        this.monthsDropdownSettings = this.setupDropdownSettings(
            "month_id",
            "month_name"
        );
    }

    protected setupDaysDropdownSettings() {
        this.daysDropdownSettings = this.setupDropdownSettings(
            "day_id",
            "day_name"
        );
    }

    protected setupWeeksDropdownSettings() {
        this.weeksDropdownSettings = this.setupDropdownSettings(
            "week_id",
            "week_name"
        );
    }

    protected setupDaysOfWeekDropdownSettings() {
        this.daysOfWeekDropdownSettings = this.setupDropdownSettings(
            "dayOfWeek_id",
            "dayOfWeek_name"
        );
    }

    protected setupQuartersOfYearDropdownSettings() {
        this.quartersOfYearDropdownSettings = this.setupDropdownSettings(
            "quarterOfYear_id",
            "quarterOfYear_name"
        );
    }

    protected setupDaysOfYearDropdownSettings() {
        this.daysOfYearDropdownSettings = this.setupDropdownSettings(
            "dayOfYear_id",
            "dayOfYear_name"
        );
    }

    protected setupHoursDropdownSettings() {
        this.hoursDropdownSettings = this.setupDropdownSettings(
            "hour_id",
            "hour_name"
        );
    }

    protected setupMinutesDropdownSettings() {
        this.minutesDropdownSettings = this.setupDropdownSettings(
            "minute_id",
            "minute_name"
        );
    }

    protected setupSecondsDropdownSettings() {
        this.secondsDropdownSettings = this.setupDropdownSettings(
            "second_id",
            "second_name"
        );
    }

    protected setupDropdownSettings(idField: string, textField: string) {
        const dropdownSettings = {
            singleSelection: false,
            idField: idField,
            textField: textField,
            selectAllText: "Seleccionar todos",
            unSelectAllText: "Deseleccionar todos",
            itemsShowLimit: 1,
            allowSearchFilter: true
        };
        return dropdownSettings;
    }
    /* ==========================
        Handle company selection
       ========================== */
    protected companySelected(company: any) {
        this.selectedCompaniesIds.push(company["company_id"]);
        this.getCampaignsForCompanies();
    }

    protected companyDeselected(company: any) {
        this.removeItemFromArray(
            company["company_id"],
            this.selectedCompaniesIds
        );
        if (this.arrayIsEmpty(this.selectedCompaniesIds)) {
            this.getAllCampaigns();
        } else {
            this.getCampaignsForCompanies();
        }
    }

    protected getCampaignsForCompanies() {
        this.statisticsService
            .getCampaingsForCompany(this.selectedCompaniesIds)
            .subscribe(data => {
                this.campaignsDropdown = [];
                this.insertIntoDropdown(EntityType.campaign, data);
            });
    }

    protected selectAllCompanies() {
        for (var index in this.companiesDropdown) {
            this.selectedCompaniesIds.push(
                this.companiesDropdown[index]["company_id"]
            );
        }
        this.getAllCampaigns();
    }

    protected deselectAllCompanies() {
        this.selectedCompaniesIds = [];
        this.getAllCampaigns();
    }

    protected getAllCampaigns() {
        this.statisticsService
            .getCampaingsForCompany(
                this.getIdsFromDropdown(this.companiesDropdown, "company_id")
            )
            .subscribe(
                data => {
                    this.insertIntoDropdown(EntityType.campaign, data);
                },
                error => {
                    console.log(error);
                    this.toastr.error("Error de conexión");
                }
            );
    }

    /* ===========================
        Handle campaign selection
       =========================== */
    protected campaignSelected(campaign: any) {
        this.selectedCampaignsIds.push(campaign["campaign_id"]);
    }

    protected campaignDeselected(campaign: any) {
        this.removeItemFromArray(
            campaign["campaign_id"],
            this.selectedCampaignsIds
        );
    }

    protected selectAllCampaigns() {
        for (var index in this.campaignsDropdown) {
            this.selectedCampaignsIds.push(
                this.campaignsDropdown[index]["campaign_id"]
            );
        }
    }

    protected deselectAllCampaigns() {
        this.selectedCampaignsIds = [];
    }

    /* ==========================
        Handle channel selection
       ========================== */
    protected channelSelected(channel: any) {
        this.selectedChannelsIds.push(channel["channel_id"]);
        this.getIntegratorsForChannels();
    }

    protected channelDeselected(channel: any) {
        this.removeItemFromArray(
            channel["channel_id"],
            this.selectedChannelsIds
        );
        if (this.arrayIsEmpty(this.selectedChannels)) {
            this.getAllIntegrators();
        } else {
            this.getIntegratorsForChannels();
        }
    }

    protected selectAllChannels() {
        for (var index in this.channelsDropdown) {
            this.selectedChannelsIds.push(
                this.channelsDropdown[index]["channel_id"]
            );
        }
        this.getAllIntegrators();
    }

    protected deselectAllChannels() {
        this.selectedChannelsIds = [];
        this.getAllIntegrators();
    }

    protected getAllIntegrators() {
        this.statisticsService
            .getIntegrators(
                this.getIdsFromDropdown(this.channelsDropdown, "channel_id")
            )
            .subscribe(
                data => {
                    this.insertIntoDropdown(EntityType.integrator, data);
                },
                error => {
                    console.log(error);
                    this.toastr.error(
                        "No se pudieron obtener los integradores."
                    );
                }
            );
    }

    protected getIntegratorsForChannels() {
        this.statisticsService
            .getIntegrators(this.selectedChannelsIds)
            .subscribe(data => {
                this.integratorsDropdown = [];
                this.insertIntoDropdown(EntityType.integrator, data);
                console.log(data);
            });
    }

    /* =============================
        Handle integrators selection
       ============================= */
    protected integratorSelected(integrator: any) {
        this.selectedIntegratorsIds.push(integrator["integrator_id"]);
    }

    protected integratorDeselected(integrator: any) {
        this.removeItemFromArray(
            integrator["integrator_id"],
            this.selectedIntegratorsIds
        );
    }

    protected selectAllIntegrators() {
        for (var index in this.integratorsDropdown) {
            this.selectedIntegratorsIds.push(
                this.integratorsDropdown[index]["integrator_id"]
            );
        }
    }

    protected deselectAllIntegrators() {
        this.selectedIntegratorsIds = [];
    }

    protected insertIntoDropdown(entityType: EntityType, data: Object) {
        switch (entityType) {
            case EntityType.company:
                for (var index in data) {
                    this.companiesDropdown.push({
                        company_id: data[index]["_idCompany"],
                        company_name: data[index]["_name"]
                    });
                }
                console.log(this.companiesDropdown);
                break;
            case EntityType.campaign:
                this.campaignsDropdown = [];
                for (var index in data) {
                    this.campaignsDropdown.push({
                        campaign_id: data[index]["_idCampaign"],
                        campaign_name: data[index]["_nameCampaign"]
                    });
                }
                break;
            case EntityType.channel:
                for (var index in data) {
                    this.channelsDropdown.push({
                        channel_id: data[index]["_id"],
                        channel_name: data[index]["_nameChannel"]
                    });
                }
                break;
            case EntityType.integrator:
                this.integratorsDropdown = [];
                for (var index in data) {
                    this.integratorsDropdown.push({
                        integrator_id: data[index]["id"],
                        integrator_name: data[index]["nameIntegrator"]
                    });
                }
                break;
        }
    }

    protected removeItemFromArray(item: Number, array: Number[]) {
        for (var i = 0; i < array.length; i++) {
            if (item == array[i]) {
                array.splice(i, 1);
            }
        }
    }

    protected removeObjectFromArray<T>(id: any, array: any[], key: T) {
        for (var i = 0; i < array.length; i++) {
            if (id == array[i][key]) {
                array.splice(i, 1);
            }
        }
    }

    protected arrayIsEmpty(array) {
        return !Array.isArray(array) || !array.length;
    }

    protected getIdsFromDropdown<T>(dropdown: any[], indexName: T) {
        var ids = [];
        dropdown.forEach(element => {
            ids.push(element[indexName]);
        });
        return ids;
    }

    /* =============================
           Handle years selection
       ============================= */
    protected yearSelected(year: any) {
        this.selectedYearsIds.push(year["year_id"]);
        console.log(this.selectedYearsIds);
    }

    protected yearDeselected(year: any) {
        this.removeItemFromArray(year["year_id"], this.selectedYearsIds);
    }

    protected selectAllYears() {
        for (var index in this.yearsDropdown) {
            this.selectedYearsIds.push(this.yearsDropdown[index]["year_id"]);
        }
    }

    protected deselectAllYears() {
        this.selectedYearsIds = [];
    }

    /* =============================
           Handle months selection
       ============================= */
    protected monthSelected(month: any) {
        this.selectedMonthsIds.push(month["month_id"]);
        console.log(this.selectedMonthsIds);
    }

    protected monthDeselected(month: any) {
        this.removeItemFromArray(month["month_id"], this.selectedMonthsIds);
    }

    protected selectAllMonths() {
        for (var index in this.monthsDropdown) {
            this.selectedMonthsIds.push(this.monthsDropdown[index]["month_id"]);
        }
    }

    protected deselectAllMonths() {
        this.selectedMonthsIds = [];
    }

    /* =============================
           Handle days selection
       ============================= */
    protected daySelected(day: any) {
        this.selectedDaysIds.push(day["day_id"]);
        console.log(this.selectedDaysIds);
    }

    protected dayDeselected(day: any) {
        this.removeItemFromArray(day["day_id"], this.selectedDaysIds);
    }

    protected selectAllDays() {
        for (var index in this.daysDropdown) {
            this.selectedDaysIds.push(this.daysDropdown[index]["day_id"]);
        }
    }

    protected deselectAllDays() {
        this.selectedDaysIds = [];
    }

    /* =============================
           Handle weeks selection
       ============================= */
    protected weekSelected(week: any) {
        this.selectedWeeksIds.push(week["week_id"]);
        console.log(this.selectedWeeksIds);
    }

    protected weekDeselected(week: any) {
        this.removeItemFromArray(week["week_id"], this.selectedWeeksIds);
    }

    protected selectAllWeeks() {
        for (var index in this.weeksDropdown) {
            this.selectedWeeksIds.push(this.weeksDropdown[index]["week_id"]);
        }
    }

    protected deselectAllWeeks() {
        this.selectedWeeksIds = [];
    }

    /* ===================================
           Handle days of weeks selection
       =================================== */
    protected dayOfWeekSelected(dayOfWeek: any) {
        this.selectedDaysOfWeekIds.push(dayOfWeek["dayOfWeek_id"]);
        console.log(this.selectedDaysOfWeekIds);
    }

    protected dayOfWeekDeselected(dayOfWeek: any) {
        this.removeItemFromArray(
            dayOfWeek["dayOfWeek_id"],
            this.selectedDaysOfWeekIds
        );
    }

    protected selectAllDaysOfWeek() {
        for (var index in this.daysOfWeekDropdown) {
            this.selectedDaysOfWeekIds.push(
                this.daysOfWeekDropdown[index]["dayOfWeek_id"]
            );
        }
    }

    protected deselectAllDaysOfWeek() {
        this.selectedDaysOfWeekIds = [];
    }

    /* ======================================
           Handle quarters of year selection
       ====================================== */
    protected quarterOfYearSelected(quarterOfYear: any) {
        this.selectedQuartersOfYearIds.push(quarterOfYear["quarterOfYear_id"]);
        console.log(this.selectedQuartersOfYearIds);
    }

    protected quarterOfYearDeselected(quarterOfYear: any) {
        this.removeItemFromArray(
            quarterOfYear["quarterOfYear_id"],
            this.selectedQuartersOfYearIds
        );
    }

    protected selectAllQuartersOfYear() {
        for (var index in this.quartersOfYearDropdown) {
            this.selectedQuartersOfYearIds.push(
                this.quartersOfYearDropdown[index]["quarterOfYear_id"]
            );
        }
    }

    protected deselectAllQuartersOfYear() {
        this.selectedQuartersOfYearIds = [];
    }

    /* ======================================
           Handle days of year selection
       ====================================== */
    protected dayOfYearSelected(dayOfYear: any) {
        this.selectedDaysOfYearIds.push(dayOfYear["dayOfYear_id"]);
        console.log(this.selectedDaysOfYearIds);
    }

    protected dayOfYearDeselected(dayOfYear: any) {
        this.removeItemFromArray(
            dayOfYear["dayOfYear_id"],
            this.selectedDaysOfYearIds
        );
    }

    protected selectAllDaysOfYear() {
        for (var index in this.daysOfYearDropdown) {
            this.selectedDaysOfYearIds.push(
                this.daysOfYearDropdown[index]["dayOfYear_id"]
            );
        }
    }

    protected deselectAllDaysOfYear() {
        this.selectedDaysOfYearIds = [];
    }

    /* =============================
           Handle hours selection
       ============================= */
    protected hourSelected(hour: any) {
        this.selectedHoursIds.push(hour["hour_id"]);
        console.log(this.selectedHoursIds);
    }

    protected hourDeselected(hour: any) {
        this.removeItemFromArray(hour["hour_id"], this.selectedHoursIds);
    }

    protected selectAllHours() {
        for (var index in this.hoursDropdown) {
            this.selectedHoursIds.push(this.hoursDropdown[index]["hour_id"]);
        }
    }

    protected deselectAllHours() {
        this.selectedHoursIds = [];
    }

    /* ===============================
           Handle minutes selection
       =============================== */
    protected minuteSelected(minute: any) {
        this.selectedMinutesIds.push(minute["minute_id"]);
        console.log(this.selectedMinutesIds);
    }

    protected minuteDeselected(minute: any) {
        this.removeItemFromArray(minute["minute_id"], this.selectedMinutesIds);
    }

    protected selectAllMinutes() {
        for (var index in this.minutesDropdown) {
            this.selectedMinutesIds.push(
                this.minutesDropdown[index]["minute_id"]
            );
        }
    }

    protected deselectAllMinutes() {
        this.selectedMinutesIds = [];
    }

    /* ===============================
           Handle seconds selection
       =============================== */
    protected secondSelected(second: any) {
        this.selectedSecondsIds.push(second["second_id"]);
        console.log(this.selectedSecondsIds);
    }

    protected secondDeselected(second: any) {
        this.removeItemFromArray(second["second_id"], this.selectedSecondsIds);
    }

    protected selectAllSeconds() {
        for (var index in this.secondsDropdown) {
            this.selectedSecondsIds.push(
                this.secondsDropdown[index]["second_id"]
            );
        }
    }

    protected deselectAllSeconds() {
        this.selectedSecondsIds = [];
    }
}
