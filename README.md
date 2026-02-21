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

Coloca tu llave pública de Apklis en:
```
android/app/src/main/assets/license_private_key.pub
```

### 3. Añadir permisos en AndroidManifest.xml

En `android/app/src/main/AndroidManifest.xml`, dentro de `<manifest>`:
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

## Uso directo en HTML (sin bundler)

```html
<script src="capacitor-plugin-apklis/dist/plugin.js"></script>
<script>
const checkLicense = async () => {
    const result = await capacitorPluginApklis.ApklisLicense.verifyLicense({
        packageName: 'com.tuempresa.tuapp'
    });
    console.log(result);
};
</script>
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
