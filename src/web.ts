import { ApklisPlugin, ApklisLicenseResponse, VerifyLicenseOptions, PurchaseLicenseOptions } from './definitions';

export class ApklisWeb implements ApklisPlugin {
  async verifyLicense(options: VerifyLicenseOptions): Promise<ApklisLicenseResponse> {
    console.log('Apklis verifyLicense not available on web', options.packageName);
    return {
      paid: false,
      error: 'Not available on web platform'
    };
  }

  async purchaseLicense(options: PurchaseLicenseOptions): Promise<ApklisLicenseResponse> {
    console.log('Apklis purchaseLicense not available on web', options.licenseUuid);
    return {
      paid: false,
      error: 'Not available on web platform'
    };
  }
}
