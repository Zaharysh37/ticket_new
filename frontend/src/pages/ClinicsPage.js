import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, message, Spin, Popconfirm, Empty, Select, Tag } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ClearOutlined } from '@ant-design/icons';
import {
    getClinics,
    getClinicById,
    createClinic,
    updateClinic,
    deleteClinic,
    getDoctors
} from '../services/api';

const { Option } = Select;

const ClinicsPage = () => {
    const [clinics, setClinics] = useState([]);
    const [doctors, setDoctors] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingClinicId, setEditingClinicId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [form] = Form.useForm();

    // Состояния для фильтров
    const [filters, setFilters] = useState({
        name: null,
        address: null,
        specializations: []
    });

    useEffect(() => {
        fetchClinics();
        fetchDoctorsList();
    }, []);

    const fetchClinics = async () => {
        try {
            setLoading(true);
            const data = await getClinics();
            setClinics(data || []);
        } catch (error) {
            message.error('Ошибка загрузки клиник');
        } finally {
            setLoading(false);
        }
    };

    const fetchDoctorsList = async () => {
        try {
            const data = await getDoctors();
            setDoctors(data || []);
        } catch (error) {
            message.error('Ошибка загрузки врачей');
        }
    };

    const checkClinicExists = (name, address, excludeId = null) => {
        return clinics.some(c =>
            c.name === name &&
            c.address === address &&
            c.id !== excludeId
        );
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();

            if (checkClinicExists(values.name, values.address)) {
                message.error('Клиника с таким названием и адресом уже существует');
                return;
            }

            setLoading(true);
            const clinicData = {
                name: values.name,
                address: values.address,
                doctorIds: values.doctorIds || []
            };
            await createClinic(clinicData);
            message.success('Клиника добавлена успешно');
            resetModal();
            await fetchClinics();
        } catch (error) {
            message.error('Ошибка добавления клиники');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();

            if (checkClinicExists(values.name, values.address, editingClinicId)) {
                message.error('Клиника с таким названием и адресом уже существует');
                return;
            }

            setLoading(true);
            const clinicData = {
                name: values.name,
                address: values.address,
                doctorIds: values.doctorIds || []
            };
            await updateClinic(editingClinicId, clinicData);
            message.success('Данные клиники обновлены успешно');
            resetModal();
            await fetchClinics();
        } catch (error) {
            message.error('Ошибка обновления данных клиники');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteClinic(id);
            message.success('Клиника удалена успешно');
            await fetchClinics();
        } catch (error) {
            message.error('Ошибка удаления клиники');
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const clinic = await getClinicById(id);
            form.setFieldsValue({
                name: clinic.name,
                address: clinic.address,
                doctorIds: clinic.doctorDtos?.map(d => d.id) || []
            });
            setEditingClinicId(id);
            setIsModalVisible(true);
        } catch (error) {
            message.error('Ошибка загрузки данных клиники');
        } finally {
            setLoading(false);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setEditingClinicId(null);
        setIsModalVisible(false);
    };

    const renderFilteredDoctors = (doctorDtos) => {
        if (!doctorDtos || doctorDtos.length === 0) {
            return <Tag color="default">Нет врачей</Tag>;
        }

        // Фильтруем врачей по выбранным специализациям
        const filteredDoctors = filters.specializations.length > 0
            ? doctorDtos.filter(d => filters.specializations.includes(d.specialization))
            : doctorDtos;

        if (filteredDoctors.length === 0) {
            return <Tag color="orange">Нет врачей выбранных специализаций</Tag>;
        }

        return (
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 4 }}>
                {filteredDoctors.map(doctor => (
                    <Tag key={doctor.id} color="blue">
                        {doctor.name} ({doctor.specialization})
                    </Tag>
                ))}
            </div>
        );
    };

    const allSpecializations = [...new Set(doctors.map(d => d.specialization))];

    const columns = [
        {
            title: 'Название',
            dataIndex: 'name',
            key: 'name',
            width: 200,
            filters: [...new Set(clinics.map(c => c.name))].map(name => ({
                text: name,
                value: name,
            })),
            onFilter: (value, record) => record.name === value,
            filteredValue: filters.name || null,
        },
        {
            title: 'Адрес',
            dataIndex: 'address',
            key: 'address',
            width: 250,
            filters: [...new Set(clinics.map(c => c.address))].map(address => ({
                text: address,
                value: address,
            })),
            onFilter: (value, record) => record.address === value,
            filteredValue: filters.address || null,
        },
        {
            title: 'Врачи',
            dataIndex: 'doctorDtos',
            key: 'doctors',
            width: 300,
            render: renderFilteredDoctors,
            filters: allSpecializations.map(spec => ({
                text: spec,
                value: spec,
            })),
            onFilter: (value, record) => true, // Фильтрация делается в render
            filteredValue: filters.specializations,
            filterSearchPlaceholder: 'Поиск по специализации'
        },
        {
            title: 'Действия',
            key: 'actions',
            fixed: 'right',
            width: 80,
            align: 'center',
            render: (_, record) => (
                <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    gap: 8
                }}>
                    <Button
                        type="text"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record.id)}
                        style={{
                            color: '#1890ff',
                            padding: '0 4px'
                        }}
                    />
                    <Popconfirm
                        title="Удалить клинику?"
                        onConfirm={() => handleDelete(record.id)}
                        okText="Да"
                        cancelText="Нет"
                    >
                        <Button
                            type="text"
                            icon={<DeleteOutlined />}
                            style={{
                                color: '#ff4d4f',
                                padding: '0 4px'
                            }}
                        />
                    </Popconfirm>
                </div>
            ),
        },
    ];

    const handleTableChange = (pagination, filters) => {
        setFilters({
            name: filters.name || null,
            address: filters.address || null,
            specializations: filters.doctors || []
        });
    };

    const resetAllFilters = () => {
        setFilters({
            name: null,
            address: null,
            specializations: []
        });
    };

    return (
        <Spin spinning={loading}>
            <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => setIsModalVisible(true)}
                >
                    Добавить клинику
                </Button>
                <Button
                    icon={<ClearOutlined />}
                    onClick={resetAllFilters}
                    disabled={!filters.name && !filters.address && filters.specializations.length === 0}
                >
                    Сбросить все фильтры
                </Button>
            </div>

            <Table
                columns={columns}
                dataSource={clinics}
                rowKey="id"
                loading={loading}
                scroll={{ x: 800 }}
                pagination={{ pageSize: 5 }}
                bordered
                locale={{
                    filterReset: 'Сбросить',
                    filterConfirm: 'ОК',
                    emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Нет данных" />
                }}
                onChange={handleTableChange}
            />

            <Modal
                title={editingClinicId ? 'Редактирование клиники' : 'Новая клиника'}
                visible={isModalVisible}
                onOk={editingClinicId ? handleUpdate : handleCreate}
                onCancel={resetModal}
                okText={editingClinicId ? 'Обновить' : 'Создать'}
                cancelText="Отмена"
                confirmLoading={loading}
                maskClosable={false}
                width={600}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="name"
                        label="Название клиники"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите название клиники!' },
                            { min: 3, message: 'Название должно содержать не менее 3 символов' }
                        ]}
                    >
                        <Input placeholder="Введите название клиники" />
                    </Form.Item>

                    <Form.Item
                        name="address"
                        label="Адрес"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите адрес клиники!' },
                            { min: 5, message: 'Адрес должен содержать не менее 5 символов' }
                        ]}
                    >
                        <Input placeholder="Введите адрес клиники" />
                    </Form.Item>

                    <Form.Item
                        name="doctorIds"
                        label="Врачи клиники"
                    >
                        <Select
                            mode="multiple"
                            placeholder="Выберите врачей"
                            optionFilterProp="children"
                            showSearch
                            filterOption={(input, option) =>
                                option.children.toLowerCase().includes(input.toLowerCase())
                            }
                        >
                            {doctors.map(doctor => (
                                <Option key={doctor.id} value={doctor.id}>
                                    {doctor.name} ({doctor.specialization})
                                </Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </Spin>
    );
};

export default ClinicsPage;