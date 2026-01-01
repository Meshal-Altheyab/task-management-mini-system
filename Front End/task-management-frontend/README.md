# Task Management Front-End (Angular)

هذا فرونت جاهز لمشروع **Project-Task-Management** (Spring Boot) اللي عندك.

## 1) المتطلبات
- **Node.js LTS حديث (يفضل v20+ أو v22)**
- Angular CLI (نثبتها بالأمر تحت)

## 2) تشغيل المشروع
افتح Terminal داخل فولدر المشروع ثم:

```bash
npm install
npm start
```

- راح يشتغل على: `http://localhost:4200`
- سكربت `npm start` يشغّل `ng serve` مع **proxy** جاهز (`proxy.conf.json`) عشان يتفادى CORS.

## 3) تشغيل الباك اند
شغل Spring Boot على المنفذ `8080`.

> الفرونت يرسل requests على `/api/...` والـ proxy يحولها تلقائياً لـ `http://localhost:8080`.

## 4) الصفحات الموجودة
- **/login** تسجيل دخول
- **/register** تسجيل جديد
- **/my-tasks** مهامي + جدول + زر تفاصيل + تعديل + حذف
- **/create-task** صفحة إضافة تاسك لحالها
- **/task/:id** صفحة تفاصيل التاسك
- **/edit-task/:id** صفحة تعديل التاسك
- **/all-tasks** كل التاسكات (تشتغل فقط إذا حسابك Admin)
- **/reports** تنزيل التقارير (PDF / XLSX / RTF)

## 5) ملاحظة عن Admin
في الباك اند عندك endpoint `/api/tasks/all` محمي بـ `hasRole('ADMIN')`.
يعني لو المستخدم عادي (USER) راح يطلع لك 403.

---

ملاحظة: صفحة **التقارير** فيها خيارات All* (Admin) — لو انت مو Admin بيرجع 403.
