export interface ApklisLicenseResponse {
  success?: boolean;
  paid: boolean;
  license?: string;
  username?: string;
  error?: string;
  statusCode?: number;
}

export interface VerifyLicenseOptions {
  packageName: string;
}

export interface PurchaseLicenseOptions {
  licenseUuid: string;
}

export interface ApklisPlugin {
  verifyLicense(options: VerifyLicenseOptions): Promise<ApklisLicenseResponse>;
  purchaseLicense(options: PurchaseLicenseOptions): Promise<ApklisLicenseResponse>;
}
