# Capacitor Plugin Apklis

Plugin de Capacitor para validar licencias de Apklis en aplicaciones Android.

## Instalación

### En un proyecto Capacitor existente

```bash
npm install capacitor-plugin-apklis
npx cap sync android
```

### Desde código fuente (desarrollo local)

```bash
# Clonar o copiar el plugin a tu proyecto
cd tu-proyecto

# Instalar como dependencia local
npm install ./capacitor-plugin-apklis

# O añadir a package.json manualmente:
# "capacitor-plugin-apklis": "file:./capacitor-plugin-apklis"

# Sincronizar con Android
npx cap sync android
```

## Compilar el plugin

```bash
cd capacitor-plugin-apklis

# Instalar dependencias
npm install

# Compilar
npm run build
```

Esto genera:
- `dist/esm/index.js` - Módulo ES
- `dist/plugin.cjs.js` - Módulo CommonJS
- `dist/plugin.js` - Bundle IIFE

## Configuración de Android

### 1. Añadir JitPack al build.gradle

En `android/build.gradle`:
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

### 2. Copiar la llave pública

Copia tu llave pública de Apklis a tu proyecto de app (NO al plugin):

```
tu-proyecto/android/app/src/main/assets/license_private_key.pub
```

> El plugin incluye una llave de ejemplo en `android/src/main/assets/` solo como referencia.

### 3. Añadir permisos en AndroidManifest.xml de tu app

> Estas configuraciones van en el proyecto de tu app, **no en el plugin**.

En `tu-proyecto/android/app/src/main/AndroidManifest.xml`, dentro de `<manifest>`:
```xml
<queries>
    <package android:name="cu.uci.android.apklis" />
</queries>
```

Para Android 16+:
```xml
<queries>
    <intent>
        <action android:name="android.accounts.AccountAuthenticator" />
    </intent>
    <intent>
        <action android:name="android.intent.action.VIEW" />
        <data android:scheme="apklis" />
    </intent>
</queries>
```

## Uso en JavaScript/TypeScript

```javascript
import { ApklisLicense } from 'capacitor-plugin-apklis';

// Verificar licencia existente
const checkLicense = async () => {
    const result = await ApklisLicense.verifyLicense({
        packageName: 'com.tuempresa.tuapp'
    });
    
    if (result.paid) {
        console.log('Licencia activa:', result.license);
        console.log('Usuario:', result.username);
        return true;
    } else {
        console.log('Sin licencia:', result.error);
        return false;
    }
};

// Comprar licencia
const buyLicense = async (licenseUuid) => {
    const result = await ApklisLicense.purchaseLicense({
        licenseUuid: licenseUuid
    });
    
    if (result.paid && result.success) {
        console.log('Compra exitosa!');
        return true;
    } else {
        console.log('Error:', result.error);
        return false;
    }
};
```

## Uso directo en HTML (Vanilla JS - sin bundler)

### Opción 1: Usando Capacitor directamente

Capacitor se inyecta automáticamente cuando compilar para Android. No necesitas importar nada:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Mi App</title>
</head>
<body>
    <script>
        // Esperar a que Capacitor esté listo
        document.addEventListener('DOMContentLoaded', async () => {
            // Verificar si estamos en Android
            if (typeof Capacitor !== 'undefined' && Capacitor.isNativePlatform()) {
                // Registrar el plugin
                const ApklisLicense = Capacitor.registerPlugin('ApklisLicense');
                
                // Hacerlo global
                window.ApklisLicense = ApklisLicense;
                
                // Verificar licencia
                await checkLicense();
            } else {
                console.log('No es plataforma nativa (modo web)');
            }
        });

        async function checkLicense() {
            try {
                const result = await window.ApklisLicense.verifyLicense({
                    packageName: 'com.tuempresa.tuapp'
                });
                
                if (result.paid) {
                    console.log('Licencia activa:', result.license);
                    console.log('Usuario:', result.username);
                    enablePremiumFeatures();
                } else {
                    console.log('Sin licencia:', result.error);
                    showPurchaseOption();
                }
            } catch (e) {
                console.error('Error:', e);
            }
        }

        async function buyLicense(licenseUuid) {
            try {
                const result = await window.ApklisLicense.purchaseLicense({
                    licenseUuid: licenseUuid
                });
                
                if (result.paid && result.success) {
                    console.log('Compra exitosa!');
                    enablePremiumFeatures();
                } else {
                    console.log('Error:', result.error);
                }
            } catch (e) {
                console.error('Error:', e);
            }
        }

        function enablePremiumFeatures() {
            document.body.classList.add('is-premium');
        }

        function showPurchaseOption() {
            // Mostrar modal de compra
        }
    </script>
</body>
</html>
```

### Opción 2: Archivo JS separado (recomendado)

Crea un archivo `apklis.js`:

```javascript
// apklis.js
let ApklisLicense = null;

async function initApklis() {
    if (typeof Capacitor !== 'undefined' && Capacitor.isNativePlatform()) {
        ApklisLicense = Capacitor.registerPlugin('ApklisLicense');
        return true;
    }
    return false;
}

async function verifyLicense(packageName) {
    if (!ApklisLicense) {
        return { paid: false, error: 'Plugin no disponible' };
    }
    return await ApklisLicense.verifyLicense({ packageName });
}

async function purchaseLicense(licenseUuid) {
    if (!ApklisLicense) {
        return { paid: false, error: 'Plugin no disponible' };
    }
    return await ApklisLicense.purchaseLicense({ licenseUuid });
}

// Exportar funciones globalmente
window.initApklis = initApklis;
window.verifyLicense = verifyLicense;
window.purchaseLicense = purchaseLicense;
```

Uso en tu HTML:

```html
<!DOCTYPE html>
<html>
<head>
    <title>Mi App</title>
</head>
<body>
    <button onclick="onBuyClick()">Comprar Premium</button>
    
    <script src="apklis.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', async () => {
            const ready = await initApklis();
            
            if (ready) {
                const result = await verifyLicense('com.tuempresa.tuapp');
                console.log('Licencia:', result);
                
                if (result.paid) {
                    enablePremiumFeatures();
                }
            }
        });

        async function onBuyClick() {
            const result = await purchaseLicense('UUID-DE-LICENCIA');
            if (result.paid) {
                alert('Premium activado!');
                enablePremiumFeatures();
            }
        }

        function enablePremiumFeatures() {
            document.body.classList.add('is-premium');
        }
    </script>
</body>
</html>
```

## Respuesta de la API

### verifyLicense
```typescript
interface ApklisLicenseResponse {
    paid: boolean;        // true si tiene licencia activa
    license?: string;     // nombre de la licencia
    username?: string;    // usuario de Apklis
    error?: string;       // mensaje de error si aplica
    statusCode?: number;  // código HTTP de error
}
```

### purchaseLicense
```typescript
interface ApklisLicenseResponse {
    success?: boolean;    // true si la compra fue exitosa
    paid: boolean;        // true si está pagado
    license?: string;     // nombre de la licencia
    username?: string;    // usuario de Apklis
    error?: string;       // mensaje de error si aplica
}
```

## Códigos de error

| Código | Descripción |
|--------|-------------|
| 402 | Debe pagar la licencia |
| 403 | Credenciales no reconocidas (abre Apklis y autentícate) |
| 404 | Grupo de licencias no publicado |
| 400 | Timeout en el pago (reintentar) |

## Configuración en Apklis Console

1. Ve a https://console.apklis.cu
2. Crea un grupo de licencias
3. Usa el package name de tu app
4. Descarga la llave pública y colócala en `android/app/src/main/assets/`
5. Publica el grupo de licencias

## Estructura del plugin

```
capacitor-plugin-apklis/
├── src/
│   ├── index.ts          # Punto de entrada
│   ├── definitions.ts    # Interfaces TypeScript
│   └── web.ts            # Implementación web (stub)
├── android/
│   ├── build.gradle      # Configuración Gradle
│   └── src/main/kotlin/
│       └── com/apklis/plugin/
│           └── ApklisLicensePlugin.kt  # Implementación Android
├── dist/                  # Archivos compilados
├── package.json
├── tsconfig.json
└── rollup.config.mjs
```

## Dependencias

El plugin depende de:
- `@capacitor/core` >= 6.0.0
- `com.github.z17-cuba:ApklisLicenseValidator:v.1.0.3` (desde JitPack)

## Licencia

MIT
