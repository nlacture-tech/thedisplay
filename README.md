# Signage Kiosk (Android WebView)

แอป kiosk เต็มจอสำหรับจอ Android — โหลดหน้า `display.html`, กันจอดับ, รีโหลดเองเมื่อเน็ตหลุด,
เปิดเองตอนบูต. **แตะมุมซ้ายบน 5 ครั้งเร็วๆ** เพื่อเปิดหน้าตั้งค่า URL

## วิธีได้ไฟล์ .apk (ฟรี ไม่ต้องลงโปรแกรม) — ผ่าน GitHub Actions

1. เข้า github.com → New repository
2. Upload files → ลากไฟล์ทั้งหมดในโฟลเดอร์นี้ขึ้นไป → Commit
3. แท็บ **Actions** → workflow **Build APK** จะรันเอง (~3-5 นาที)
4. เปิดงานที่เสร็จ → **Artifacts** → ดาวน์โหลด `signage-kiosk-apk` → ได้ `app-debug.apk`
5. เอา APK ไปลงที่จอผ่าน USB (เปิดอนุญาตติดตั้งจากแหล่งที่ไม่รู้จักก่อน)

## หรือบิลด์เองด้วย Android Studio
เปิดโฟลเดอร์นี้ใน Android Studio → Build → Build APK(s) → ได้ไฟล์ที่
`app/build/outputs/apk/debug/app-debug.apk`

## หมายเหตุ
- ตั้งค่า URL ครั้งแรกในแอป เช่น `https://ชื่อเว็บ.netlify.app/display.html`
- เฟิร์มแวร์จอจีนบางรุ่นต้องเปิด "Autostart / 自启动" ให้แอปนี้ เพื่อให้เด้งเองตอนบูต
- เป็น debug build (เซ็นด้วย debug key) ติดตั้งใช้งานได้ปกติแบบ sideload
