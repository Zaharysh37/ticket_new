import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, message, Spin, Popconfirm } from 'antd';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import {
    getPatients,
    getPatientById,
    createPatient,
    updatePatient,
    deletePatient,
    findPatientsByFilter
} from '../services/api';

const PatientsPage = () => {
    const [patients, setPatients] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingPatientId, setEditingPatientId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [searchName, setSearchName] = useState('');
    const [searchPhone, setSearchPhone] = useState('');
    const [form] = Form.useForm();

    useEffect(() => {
        fetchPatients();
    }, []);

    const fetchPatients = async () => {
        try {
            setLoading(true);
            const data = await getPatients();
            setPatients(data || []);
        } catch (error) {
            message.error('Ошибка загрузки пациентов');
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async () => {
        try {
            setLoading(true);
            const data = await findPatientsByFilter(searchName, searchPhone);
            setPatients(data || []);
        } catch (error) {
            message.error('Ошибка поиска пациентов');
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();
            await createPatient(values);
            message.success('Пациент добавлен успешно');
            resetModal();
            await fetchPatients();
        } catch (error) {
            message.error('Ошибка добавления пациента');
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();
            await updatePatient(editingPatientId, values);
            message.success('Данные пациента обновлены успешно');
            resetModal();
            await fetchPatients();
        } catch (error) {
            message.error('Ошибка обновления данных пациента');
        }
    };

    const handleDelete = async (id) => {
        try {
            await deletePatient(id);
            message.success('Пациент удалён успешно');
            await fetchPatients();
        } catch (error) {
            message.error('Ошибка удаления пациента');
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const patient = await getPatientById(id);
            form.setFieldsValue({
                name: patient.name,
                phoneNumber: patient.phoneNumber
            });
            setEditingPatientId(id);
            setIsModalVisible(true);
        } catch (error) {
            message.error('Ошибка загрузки данных пациента');
        } finally {
            setLoading(false);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setEditingPatientId(null);
        setIsModalVisible(false);
    };

    const columns = [
        {
            title: 'Имя',
            dataIndex: 'name',
            key: 'name',
            sorter: (a, b) => a.name.localeCompare(b.name),
        },
        {
            title: 'Телефон',
            dataIndex: 'phoneNumber',
            key: 'phoneNumber',
            sorter: (a, b) => a.phoneNumber.localeCompare(b.phoneNumber),
        },
        {
            title: 'Действия',
            key: 'actions',
            render: (_, patient) => (
                <Space>
                    <Button onClick={() => handleEdit(patient.id)}>Изменить</Button>
                    <Popconfirm
                        title="Вы уверены, что хотите удалить этого пациента?"
                        onConfirm={() => handleDelete(patient.id)}
                        okText="Да"
                        cancelText="Нет"
                    >
                        <Button danger>Удалить</Button>
                    </Popconfirm>
                </Space>
            ),
        },
    ];

    return (
        <Spin spinning={loading}>
            <div style={{ marginBottom: 16, display: 'flex', gap: 16 }}>
                <Input
                    placeholder="Поиск по имени"
                    value={searchName}
                    onChange={(e) => setSearchName(e.target.value)}
                    style={{ width: 200 }}
                />
                <Input
                    placeholder="Поиск по телефону"
                    value={searchPhone}
                    onChange={(e) => setSearchPhone(e.target.value)}
                    style={{ width: 200 }}
                />
                <Button
                    type="primary"
                    icon={<SearchOutlined />}
                    onClick={handleSearch}
                >
                    Поиск
                </Button>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => setIsModalVisible(true)}
                    style={{ marginLeft: 'auto' }}
                >
                    Добавить пациента
                </Button>
            </div>

            <Table
                columns={columns}
                dataSource={patients}
                rowKey="id"
                loading={loading}
                bordered
                pagination={{ pageSize: 10 }}
            />

            <Modal
                title={editingPatientId ? 'Редактирование пациента' : 'Новый пациент'}
                visible={isModalVisible}
                onOk={editingPatientId ? handleUpdate : handleCreate}
                onCancel={resetModal}
                okText={editingPatientId ? 'Обновить' : 'Создать'}
                cancelText="Отмена"
                confirmLoading={loading}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="name"
                        label="ФИО пациента"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите ФИО пациента!' },
                            { min: 3, message: 'ФИО должно содержать не менее 3 символов' }
                        ]}
                    >
                        <Input placeholder="Введите ФИО пациента" />
                    </Form.Item>

                    <Form.Item
                        name="phoneNumber"
                        label="Номер телефона"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите номер телефона!' },
                            { pattern: /^[0-9]+$/, message: 'Номер телефона должен содержать только цифры' }
                        ]}
                    >
                        <Input placeholder="Введите номер телефона" />
                    </Form.Item>
                </Form>
            </Modal>
        </Spin>
    );
};

export default PatientsPage;