import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, message, Spin, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import {
    getClinics,
    getClinicById,
    createClinic,
    updateClinic,
    deleteClinic
} from '../services/api';

const ClinicsPage = () => {
    const [clinics, setClinics] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingClinicId, setEditingClinicId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [form] = Form.useForm();

    useEffect(() => {
        fetchClinics();
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

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();
            await createClinic(values);
            message.success('Клиника добавлена успешно');
            resetModal();
            await fetchClinics();
        } catch (error) {
            message.error('Ошибка добавления клиники');
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();
            await updateClinic(editingClinicId, values);
            message.success('Данные клиники обновлены успешно');
            resetModal();
            await fetchClinics();
        } catch (error) {
            message.error('Ошибка обновления данных клиники');
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
                address: clinic.address
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

    const columns = [
        {
            title: 'Название',
            dataIndex: 'name',
            key: 'name',
            sorter: (a, b) => a.name.localeCompare(b.name),
        },
        {
            title: 'Адрес',
            dataIndex: 'address',
            key: 'address',
            sorter: (a, b) => a.address.localeCompare(b.address),
        },
        {
            title: 'Действия',
            key: 'actions',
            render: (_, clinic) => (
                <Space>
                    <Button onClick={() => handleEdit(clinic.id)}>Изменить</Button>
                    <Popconfirm
                        title="Вы уверены, что хотите удалить эту клинику?"
                        onConfirm={() => handleDelete(clinic.id)}
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
            <Button
                type="primary"
                icon={<PlusOutlined />}
                onClick={() => setIsModalVisible(true)}
                style={{ marginBottom: 16 }}
            >
                Добавить клинику
            </Button>

            <Table
                columns={columns}
                dataSource={clinics}
                rowKey="id"
                loading={loading}
                bordered
                pagination={{ pageSize: 10 }}
            />

            <Modal
                title={editingClinicId ? 'Редактирование клиники' : 'Новая клиника'}
                visible={isModalVisible}
                onOk={editingClinicId ? handleUpdate : handleCreate}
                onCancel={resetModal}
                okText={editingClinicId ? 'Обновить' : 'Создать'}
                cancelText="Отмена"
                confirmLoading={loading}
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
                </Form>
            </Modal>
        </Spin>
    );
};

export default ClinicsPage;