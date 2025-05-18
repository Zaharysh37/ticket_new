import axios from 'axios';

const api = axios.create({
    baseURL: process.env.REACT_APP_API_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// ================== Appointments API ==================
export const getAppointments = () => api.get('/appointments').then(res => res.data);
export const getAppointmentById = (id) => api.get(`/appointments/${id}`).then(res => res.data);
export const createAppointment = (appointment) => api.post('/appointments', appointment).then(res => res.data);
export const createBulkAppointments = (appointments) => api.post('/appointments/bulk', appointments).then(res => res.data);
export const updateAppointment = (id, appointment) => api.put(`/appointments/${id}`, appointment).then(res => res.data);
export const deleteAppointment = (id) => api.delete(`/appointments/${id}`);
export const findAppointmentsByPatient = (patientName) =>
    api.get(`/appointments/filter?patientName=${patientName}`).then(res => res.data);

// ================== Doctors API ==================
export const getDoctors = () => api.get('/doctors').then(res => res.data);
export const getDoctorById = (id) => api.get(`/doctors/${id}`).then(res => res.data);
export const getDoctorByClinic = (id) => api.get(`/doctors/clinic/${id}`).then(res => res.data);
export const createDoctor = (doctor) => api.post('/doctors', doctor).then(res => res.data);
export const updateDoctor = (id, doctor) => api.put(`/doctors/${id}`, doctor).then(res => res.data);
export const deleteDoctor = (id) => api.delete(`/doctors/${id}`);
export const findAvailableDoctors = (appointmentTime, specialization) =>
    api.get(`/doctors/available?specialization=${specialization}&appointmentTime=${appointmentTime}`)
        .then(res => res.data);

// ================== Patients API ==================
export const getPatients = () => api.get('/patients').then(res => res.data);
export const getPatientById = (id) => api.get(`/patients/${id}`).then(res => res.data);
export const createPatient = (patient) => api.post('/patients', patient).then(res => res.data);
export const updatePatient = (id, patient) => api.put(`/patients/${id}`, patient).then(res => res.data);
export const deletePatient = (id) => api.delete(`/patients/${id}`);
export const findPatientsByFilter = (name, phoneNumber) => {
    const params = new URLSearchParams();
    if (name) params.append('name', name);
    if (phoneNumber) params.append('phoneNumber', phoneNumber);

    return api.get(`/patients?${params.toString()}`).then(res => res.data);
};

// ================== Clinics API ==================
export const getClinics = () => api.get('/clinics').then(res => res.data);
export const getClinicById = (id) => api.get(`/clinics/${id}`).then(res => res.data);
export const createClinic = (clinic) => api.post('/clinics', clinic).then(res => res.data);
export const updateClinic = (id, clinic) => api.put(`/clinics/${id}`, clinic).then(res => res.data);
export const deleteClinic = (id) => api.delete(`/clinics/${id}`);

api.interceptors.response.use(
    response => response,
    error => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

export default api;