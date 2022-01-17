# ZATAC Scanner
[![Alt text](https://i.postimg.cc/tgJTRK3t/logo.png)](https://e-invoice.io)

![Alt text](https://img.shields.io/badge/Depedencies-up_to_date-<COLOR>)
![Alt text](https://img.shields.io/badge/Library_version-1.0.0-orange)
![Alt text](https://img.shields.io/badge/Framework-Android-<COLOR>)

ZATAC Scanner is kotlin-based QR code scanner and parser which de-crypt TLV qr codes and parse them into their values, provided by https://e-invoice.io 

## Preview
[![ZATCA-scanner-demo.gif](./preview/ZATCA%20scanner%20demo.gif)](https://e-invoice.io)

## Features

- Easy to use.
- Theme customization.
- Ability to use the scanner view itslef for more customization and ease of integration ,but you will need to check for camera persmission your self and also you will need to add listener for image picking .
- Ability to use parsing method directly and Barcode scanner for low-level integration.

## Download
- add [library aar file] to you ``app/libs`` directory 
- then add the dependency to your app-level gradle ``build.gradle(:app)``
```sh
implementation files('libs/ZATCA_scanner-v1.0-debug.aar')
```
## How to use
To use the Scanner activity:
define in onCreate:
```sh
val activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                   val scannedQRCode = result.data?.getSerializableExtra(ZATCAScannerActivity.ZATCA_BILL_INFO) as? ZATCAQRCode)
                   //Do something with scanned QR code
                }
            }
```
then launch the activity with:
```sh
activityLauncher.launch(ZATCAScannerActivity.newIntent(this)) //this refers to activity context
```
> Note: you can use any normal intent to start the acivity but for customizations you have to use the provided intent with the activity
## Customizations
you can customize the buttons ,scan indicator background color and text color.

[![Alt text](https://i.postimg.cc/Kzgk2SN1/Screen-Shot-2022-01-17-at-3-03-23-PM.png)](/)

- provide the prefered color resource id for the buttons, the text color and the separator color and visibilty via intent
```sh
activityLauncher.launch(
                ZATCAScannerActivity.newIntent(
                    this,//activity context
                   R.color.pick_img_btn_bk_color,
                   R.color.text_color,
                    R.color.separator_color
                )
            )
```
- or if you want to use the view you can customize its theme via the following attributes 
```sh
<com.enozom.poc.e_invoice.utils.customViews.ZATCAScannerView
        android:id="@+id/zatca_sv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:zatca_btnBackgroundColor="@color/colorFAA712"
        app:zatca_separatorColor="@color/colorFAA712"
        app:zatca_textColor="@color/color398CFD" />
```

## Android Permissions

The camera permission is required for QR code scanning to function. It is automatically requested by the scanning activity. On Android 6 it is requested at runtime when the barcode scanner is first opened.

When using ZATCAScannerView, you have to request the permission manually before calling ZATCAScannerView#resume(), otherwise the camera will fail to open.

## License
Licensed under [MIT License]

Copyright (c) 2021 Enozom

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.



[MIT License]:<https://opensource.org/licenses/MIT>
[library aar file]:<./library%20file/>
