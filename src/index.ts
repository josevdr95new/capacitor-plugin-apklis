import { registerPlugin } from '@capacitor/core';
import { ApklisWeb } from './web';

import type { ApklisPlugin } from './definitions';

const ApklisLicense = registerPlugin<ApklisPlugin>('ApklisLicense', {
  web: () => Promise.resolve(new ApklisWeb()),
});

export * from './definitions';
export { ApklisLicense };
