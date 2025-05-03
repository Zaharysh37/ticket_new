import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, message, Spin, Popconfirm, Select, DatePicker, Tag, Empty, AutoComplete } from 'antd';
import { SearchOutlined, PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import {
    getAppointments,
    getAppointmentById,
    createAppointment,
    updateAppointment,
    deleteAppointment,
    findAppointmentsByPatient,
    getDoctorByClinic,
    getPatients,
    getClinics,
    findAvailableDoctors
} from '../services/api';
import moment from 'moment';
import 'moment/locale/ru';

moment.locale('ru');

const { Option } = Select;

const AppointmentsPage = () => {
    const [appointments, setAppointments] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingAppointmentId, setEditingAppointmentId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [clinicDoctors, setClinicDoctors] = useState([]);
    const [patients, setPatients] = useState([]);
    const [clinics, setClinics] = useState([]);
    const [specializations, setSpecializations] = useState([]);
    const [availableDoctors, setAvailableDoctors] = useState([]);
    const [selectedClinic, setSelectedClinic] = useState(null);
    const [form] = Form.useForm();
    const [searchValue, setSearchValue] = useState('');
    const [filteredPatients, setFilteredPatients] = useState([]);

    useEffect(() => {
        fetchInitialData();
    }, []);

    useEffect(() => {
        if (patients.length > 0) {
            setFilteredPatients(patients);
        }
    }, [patients]);

    const fetchInitialData = async () => {
        try {
            setLoading(true);
            const [appts, pts, cls] = await Promise.all([
                getAppointments(),
                getPatients(),
                getClinics()
            ]);

            setAppointments(appts || []);
            setPatients(pts || []);
            setClinics(cls || []);
        } catch (error) {
            message.error('Не удалось загрузить начальные данные');
        } finally {
            setLoading(false);
        }
    };

    const filterPatients = (inputValue) => {
        if (!inputValue) {
            setFilteredPatients(patients);
            return;
        }

        const searchText = inputValue || '';
        const filtered = patients.filter(patient =>
            patient.name.toLowerCase().includes(inputValue.toLowerCase()) ||
            patient.phoneNumber.includes(inputValue)
        );
        setFilteredPatients(filtered);
    };

    const fetchDoctorsForClinic = async (clinicId) => {
        try {
            if (clinicId) {
                const doctorsData = await getDoctorByClinic(clinicId);
                setClinicDoctors(doctorsData || []);

                const specs = [...new Set(doctorsData.map(d => d.specialization))];
                setSpecializations(specs);
                return doctorsData;
            }
            return [];
        } catch (error) {
            message.error('Не удалось загрузить врачей для выбранной клиники');
            return [];
        }
    };

    const fetchAvailableDoctors = async (specialization, dateTime) => {
        try {
            if (specialization && dateTime) {
                const formattedTime = dateTime.format("YYYY-MM-DDTHH:mm:ss");
                const data = await findAvailableDoctors(formattedTime, specialization);

                const filteredData = data.filter(doctor =>
                    clinicDoctors.some(d => d.id === doctor.id)
                );

                setAvailableDoctors(filteredData || []);
                return filteredData;
            }
            return [];
        } catch (error) {
            message.error('Ошибка при проверке доступности врачей');
            return [];
        }
    };

    const handleClinicChange = async (clinicId) => {
        setSelectedClinic(clinicId);
        setAvailableDoctors([]);
        form.setFieldsValue({
            specialization: undefined,
            doctorId: undefined,
            appointmentTime: undefined
        });

        if (clinicId) {
            await fetchDoctorsForClinic(clinicId);
        } else {
            setClinicDoctors([]);
            setSpecializations([]);
        }
    };

    const handleSpecializationChange = async (specialization) => {
        form.setFieldsValue({ doctorId: undefined });
        setAvailableDoctors([]);

        const dateTime = form.getFieldValue('appointmentTime');
        if (dateTime) {
            await fetchAvailableDoctors(specialization, dateTime);
        }
    };

    const handleDateTimeChange = async (dateTime) => {
        form.setFieldsValue({ doctorId: undefined });
        setAvailableDoctors([]);

        const specialization = form.getFieldValue('specialization');
        if (specialization && dateTime) {
            await fetchAvailableDoctors(specialization, dateTime);
        }
    };

    const validateDateTime = (_, value) => {
        if (value && value.isBefore(moment())) {
            return Promise.reject('Нельзя выбрать прошедшее время');
        }
        return Promise.resolve();
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();

            if (values.appointmentTime && values.appointmentTime.isBefore(moment())) {
                message.error('Выбранное время уже прошло');
                return;
            }

            const selectedDoctor = availableDoctors.find(d => d.id === values.doctorId);
            if (!selectedDoctor) {
                message.error('Выбранный врач недоступен в это время');
                return;
            }

            await createAppointment({
                patientId: values.patientId,
                doctorId: values.doctorId,
                clinicId: values.clinicId,
                appointmentTime: values.appointmentTime.format("YYYY-MM-DDTHH:mm:ss")
            });

            message.success('Запись успешно создана');
            resetModal();
            await fetchInitialData();
        } catch (error) {
            message.error('Ошибка при создании записи: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();

            if (values.appointmentTime && values.appointmentTime.isBefore(moment())) {
                message.error('Выбранное время уже прошло');
                return;
            }

            const selectedDoctor = availableDoctors.find(d => d.id === values.doctorId);
            if (!selectedDoctor) {
                message.error('Выбранный врач недоступен в это время');
                return;
            }

            await updateAppointment(editingAppointmentId, {
                patientId: values.patientId,
                doctorId: values.doctorId,
                clinicId: values.clinicId,
                appointmentTime: values.appointmentTime.format("YYYY-MM-DDTHH:mm:ss")
            });

            message.success('Запись успешно обновлена');
            resetModal();
            await fetchInitialData();
        } catch (error) {
            message.error('Ошибка при обновлении записи: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteAppointment(id);
            message.success('Запись успешно удалена');
            await fetchInitialData();
        } catch (error) {
            message.error('Ошибка при удалении записи: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const appointment = await getAppointmentById(id);

            await fetchDoctorsForClinic(appointment.clinicId);

            if (appointment.doctorDto?.specialization && appointment.appointmentTime) {
                await fetchAvailableDoctors(
                    appointment.doctorDto.specialization,
                    moment(appointment.appointmentTime)
                );
            }

            form.setFieldsValue({
                patientId: appointment.patientDto.id,
                clinicId: appointment.clinicId,
                doctorId: appointment.doctorDto.id,
                specialization: appointment.doctorDto.specialization,
                appointmentTime: moment(appointment.appointmentTime)
            });

            setSelectedClinic(appointment.clinicId);
            setEditingAppointmentId(id);
            setIsModalVisible(true);
        } catch (error) {
            message.error('Ошибка при загрузке данных записи');
        } finally {
            setLoading(false);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setEditingAppointmentId(null);
        setIsModalVisible(false);
        setAvailableDoctors([]);
        setSelectedClinic(null);
    };

    const handleSearch = async (value) => {
        try {
            setLoading(true);
            if (value) {
                const data = await findAppointmentsByPatient(value);
                setAppointments(data || []);
            } else {
                const data = await getAppointments();
                setAppointments(data || []);
            }
        } catch (error) {
            setAppointments([]);
        } finally {
            setLoading(false);
        }
    };

    const handleSearchChange = (value) => {
        setSearchValue(value || '');
        filterPatients(value);
        if (!value) {
            handleSearch('');
        }
    };

    const columns = [
        {
            title: 'Пациент',
            dataIndex: ['patientDto', 'name'],
            key: 'patientName',
            width: 190,
            render: (text, record) => (
                <span>
                    {text} <br />
                    <Tag color="blue">{record.patientDto.phoneNumber}</Tag>
                </span>
            ),
            filters: [...new Set(appointments.map(a => a.patientDto.name))].map(name => ({
                text: name,
                value: name,
            })),
            onFilter: (value, record) => record.patientDto.name.includes(value),
            filterSearchPlaceholder: 'Поиск',
        },
        {
            title: 'Клиника',
            dataIndex: 'clinicName',
            key: 'clinicName',
            width: 200,
            filters: [...new Set(appointments.map(a => a.clinicName))].map(name => ({
                text: name,
                value: name,
            })),
            onFilter: (value, record) => record.clinicName.includes(value),
            filterSearchPlaceholder: 'Поиск',
        },
        {
            title: 'Адрес',
            dataIndex: 'clinicAddress',
            key: 'clinicAddress',
            width: 200
        },
        {
            title: 'Врач',
            dataIndex: ['doctorDto', 'name'],
            key: 'doctorName',
            width: 190,
            render: (text, record) => (
                <span>
                    {text} <br />
                    <Tag color="green">{record.doctorDto.specialization}</Tag>
                </span>
            ),
            filters: [...new Set(appointments.map(a => a.doctorDto.name))].map(name => ({
                text: name,
                value: name,
            })),
            onFilter: (value, record) => record.doctorDto.name.includes(value),
            filterSearchPlaceholder: 'Поиск',
        },
        {
            title: 'Дата',
            dataIndex: 'appointmentTime',
            key: 'appointmentTime',
            render: (time) => <span style={{ whiteSpace: 'nowrap' }}>{moment(time).format('DD.MM.YYYY HH:mm')}</span>,
            width: 150,
            sorter: (a, b) => moment(a.appointmentTime).unix() - moment(b.appointmentTime).unix()
        },
        {
            title: 'Действия',
            key: 'actions',
            fixed: 'right',
            width: 70,
            align: 'center',
            render: (_, record) => (
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-around',
                    width: '100%'
                }}>
                    <Button
                        type="text"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record.id)}
                        style={{
                            color: '#1890ff',
                            minWidth: 24,
                            padding: '0 4px'
                        }}
                    />
                    <Popconfirm
                        title="Вы уверены, что хотите удалить эту запись?"
                        onConfirm={() => handleDelete(record.id)}
                        okText="Да"
                        cancelText="Нет"
                    >
                        <Button
                            type="text"
                            icon={<DeleteOutlined />}
                            style={{
                                color: '#ff4d4f',
                                minWidth: 24,
                                padding: '0 4px'
                            }}
                        />
                    </Popconfirm>
                </div>
            ),
        },
    ];

    const datePickerLocale = {
        lang: {
            placeholder: 'Выберите дату',
            rangePlaceholder: ['Начальная дата', 'Конечная дата'],
            locale: 'ru',
            today: 'Сегодня',
            now: null,
            backToToday: 'Вернуться к сегодняшней дате',
            ok: 'ОК',
            clear: 'Очистить',
            month: 'Месяц',
            year: 'Год',
            timeSelect: 'Выбрать время',
            dateSelect: 'Выбрать дату',
            monthSelect: 'Выбрать месяц',
            yearSelect: 'Выбрать год',
            decadeSelect: 'Выбрать десятилетие',
            yearFormat: 'YYYY',
            dateFormat: 'D M YYYY',
            dayFormat: 'D',
            dateTimeFormat: 'D M YYYY HH:mm:ss',
            monthBeforeYear: true,
            previousMonth: 'Предыдущий месяц',
            nextMonth: 'Следующий месяц',
            previousYear: 'Предыдущий год',
            nextYear: 'Следующий год',
            previousDecade: 'Предыдущее десятилетие',
            nextDecade: 'Следующее десятилетие',
            previousCentury: 'Предыдущий век',
            nextCentury: 'Следующий век',
            shortWeekDays: ['Вс', 'Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб'],
            shortMonths: [
                'Янв', 'Фев', 'Мар', 'Апр', 'Май', 'Июн',
                'Июл', 'Авг', 'Сен', 'Окт', 'Ноя', 'Дек'
            ],
        },
        timePickerLocale: {
            placeholder: 'Выберите время',
        },
    };

    return (
        <Spin spinning={loading}>
            <div style={{
                marginBottom: 16,
                display: 'flex',
                justifyContent: 'space-between',
                background: '#fff',
                padding: 16,
                borderRadius: 8
            }}>
                <div style={{
                    display: 'flex',
                    width: 400,
                    alignItems: 'center'
                }}>
                    <AutoComplete
                        options={filteredPatients.map(patient => ({
                            value: patient.name,
                            label: `${patient.name} (${patient.phoneNumber})`
                        }))}
                        style={{ flex: 1 }}
                        onSelect={(value) => {
                            setSearchValue(value);
                            handleSearch(value);
                        }}
                        onChange={handleSearchChange}
                        value={searchValue}
                        placeholder="Поиск по имени пациента"
                        allowClear
                        dropdownMatchSelectWidth={400}
                    >
                        <Input
                            style={{
                                borderRight: 'none',
                                borderRadius: '4px 0 0 4px',
                                height: 32,
                                padding: '4px 11px'
                            }}
                        />
                    </AutoComplete>
                    <Button
                        type="primary"
                        icon={<SearchOutlined />}
                        onClick={() => handleSearch(searchValue)}
                        style={{
                            borderRadius: '0 4px 4px 0',
                            height: 32,
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center'
                        }}
                    />
                </div>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => setIsModalVisible(true)}
                >
                    Новая запись
                </Button>
            </div>

            <div style={{
                background: '#fff',
                padding: 16,
                borderRadius: 8
            }}>
                <Table
                    columns={columns}
                    dataSource={appointments}
                    rowKey="id"
                    loading={loading}
                    scroll={{ x: 1100 }}
                    pagination={{
                        pageSize: 5,
                        showSizeChanger: false,
                        //showTotal: (total) => `Всего записей: ${total}`,
                    }}
                    bordered
                    locale={{
                        emptyText: <Empty description="Нет данных" />,
                        filterSearchPlaceholder: 'Поиск',
                        filterReset: 'Сбросить',
                        filterConfirm: 'ОК'
                    }}
                />
            </div>

            <Modal
                title={editingAppointmentId ? 'Редактировать запись' : 'Новая запись'}
                visible={isModalVisible}
                onOk={editingAppointmentId ? handleUpdate : handleCreate}
                onCancel={resetModal}
                okText={editingAppointmentId ? 'Обновить' : 'Создать'}
                cancelText="Отмена"
                confirmLoading={loading}
                width={700}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="patientId"
                        label="Пациент"
                        rules={[{ required: true, message: 'Пожалуйста, выберите пациента' }]}
                    >
                        <Select
                            showSearch
                            placeholder="Выберите пациента"
                            optionFilterProp="label"
                            filterOption={(input, option) => {
                                if (!option || !option.label) return false;
                                return option.label.toLowerCase().includes(input.toLowerCase());
                            }}
                            options={patients.map(patient => ({
                                value: patient.id,
                                label: `${patient.name} (${patient.phoneNumber})`
                            }))}
                        />
                    </Form.Item>

                    <Form.Item
                        name="clinicId"
                        label="Клиника"
                        rules={[{ required: true, message: 'Пожалуйста, выберите клинику' }]}
                    >
                        <Select
                            showSearch
                            placeholder="Выберите клинику"
                            optionFilterProp="label"
                            filterOption={(input, option) => {
                                if (!option || !option.label) return false;
                                return option.label.toLowerCase().includes(input.toLowerCase());
                            }}
                            onChange={handleClinicChange}
                            options={clinics.map(clinic => ({
                                value: clinic.id,
                                label: `${clinic.name} (${clinic.address})`
                            }))}
                        />
                    </Form.Item>

                    <Form.Item
                        name="specialization"
                        label="Специализация"
                        rules={[{ required: true, message: 'Пожалуйста, выберите специализацию' }]}
                    >
                        <Select
                            placeholder="Выберите специализацию"
                            onChange={handleSpecializationChange}
                            showSearch
                            disabled={!selectedClinic}
                            optionFilterProp="label"
                            filterOption={(input, option) => {
                                if (!option || !option.label) return false;
                                return option.label.toLowerCase().includes(input.toLowerCase());
                            }}
                            options={specializations.map(spec => ({
                                value: spec,
                                label: spec
                            }))}
                        />
                    </Form.Item>

                    <Form.Item
                        name="appointmentTime"
                        label="Дата и время"
                        rules={[
                            { required: true, message: 'Пожалуйста, выберите дату и время' },
                            { validator: validateDateTime }
                        ]}
                    >
                        <DatePicker
                            showTime={{
                                format: 'HH:mm',
                                minuteStep: 15,
                                hideDisabledOptions: true,
                                use12Hours: false,
                                showNow: false
                            }}
                            format="DD.MM.YYYY HH:mm"
                            style={{ width: '100%' }}
                            onChange={handleDateTimeChange}
                            disabledDate={current => current && current <= moment().endOf('day')}
                            disabledTime={current => {
                                if (current && current.isSame(moment(), 'day')) {
                                    return {
                                        disabledHours: () => [...Array(moment().hour())].map((_, i) => i),
                                        disabledMinutes: (selectedHour) => {
                                            if (selectedHour === moment().hour()) {
                                                return [...Array(moment().minute() + 1)].map((_, i) => i);
                                            }
                                            return [];
                                        }
                                    };
                                }
                                return {};
                            }}
                            disabled={!selectedClinic}
                            placeholder="Выберите дату"
                            locale={{
                                ...datePickerLocale,
                                timePickerLocale: {
                                    ...datePickerLocale.timePickerLocale,
                                    now: '',
                                },
                            }}
                            getPopupContainer={trigger => trigger.parentElement}
                        />
                    </Form.Item>

                    <Form.Item
                        name="doctorId"
                        label="Доступные врачи"
                        rules={[{ required: true, message: 'Пожалуйста, выберите врача' }]}
                    >
                        <Select
                            placeholder={availableDoctors.length ? "Выберите врача" : "Нет доступных врачей для выбранных критериев"}
                            disabled={!availableDoctors.length}
                            showSearch
                            optionFilterProp="label"
                            filterOption={(input, option) => {
                                if (!option || !option.label) return false;
                                return option.label.toLowerCase().includes(input.toLowerCase());
                            }}
                            options={availableDoctors.map(doctor => ({
                                value: doctor.id,
                                label: `${doctor.name} (${doctor.specialization})`
                            }))}
                        />
                    </Form.Item>
                </Form>
            </Modal>
        </Spin>
    );
};

export default AppointmentsPage;