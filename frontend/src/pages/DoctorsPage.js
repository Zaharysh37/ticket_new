import React, { useState, useEffect } from 'react';
import { Table, Button, Space, Modal, Form, Input, message, Spin, Popconfirm } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import {
    getDoctors,
    getDoctorById,
    createDoctor,
    updateDoctor,
    deleteDoctor
} from '../services/api';

const DoctorsPage = () => {
    const [doctors, setDoctors] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [editingDoctorId, setEditingDoctorId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [form] = Form.useForm();

    useEffect(() => {
        fetchDoctors();
    }, []);

    const fetchDoctors = async () => {
        try {
            setLoading(true);
            const data = await getDoctors();
            setDoctors(data || []);
        } catch (error) {
            message.error('Ошибка загрузки врачей');
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();
            await createDoctor(values);
            message.success('Врач добавлен успешно');
            resetModal();
            await fetchDoctors();
        } catch (error) {
            message.error('Ошибка добавления врача');
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();
            await updateDoctor(editingDoctorId, values);
            message.success('Данные врача обновлены успешно');
            resetModal();
            await fetchDoctors();
        } catch (error) {
            message.error('Ошибка обновления данных врача');
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteDoctor(id);
            message.success('Врач удалён успешно');
            await fetchDoctors();
        } catch (error) {
            message.error('Ошибка удаления врача');
        }
    };

    const handleEdit = async (id) => {
        try {
            setLoading(true);
            const doctor = await getDoctorById(id);
            form.setFieldsValue({
                name: doctor.name,
                specialization: doctor.specialization
            });
            setEditingDoctorId(id);
            setIsModalVisible(true);
        } catch (error) {
            message.error('Ошибка загрузки данных врача');
        } finally {
            setLoading(false);
        }
    };

    const resetModal = () => {
        form.resetFields();
        setEditingDoctorId(null);
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
            title: 'Специализация',
            dataIndex: 'specialization',
            key: 'specialization',
            sorter: (a, b) => a.specialization.localeCompare(b.specialization),
        },
        {
            title: 'Действия',
            key: 'actions',
            render: (_, doctor) => (
                <Space>
                    <Button onClick={() => handleEdit(doctor.id)}>Изменить</Button>
                    <Popconfirm
                        title="Вы уверены, что хотите удалить этого врача?"
                        onConfirm={() => handleDelete(doctor.id)}
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
                Добавить врача
            </Button>

            <Table
                columns={columns}
                dataSource={doctors}
                rowKey="id"
                loading={loading}
                bordered
                pagination={{ pageSize: 10 }}
            />

            <Modal
                title={editingDoctorId ? 'Редактирование врача' : 'Новый врач'}
                visible={isModalVisible}
                onOk={editingDoctorId ? handleUpdate : handleCreate}
                onCancel={resetModal}
                okText={editingDoctorId ? 'Обновить' : 'Создать'}
                cancelText="Отмена"
                confirmLoading={loading}
            >
                <Form form={form} layout="vertical">
                    <Form.Item
                        name="name"
                        label="Имя врача"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите имя врача!' },
                            { min: 3, message: 'Имя должно содержать не менее 3 символов' }
                        ]}
                    >
                        <Input placeholder="Введите имя врача" />
                    </Form.Item>

                    <Form.Item
                        name="specialization"
                        label="Специализация"
                        rules={[
                            { required: true, message: 'Пожалуйста, укажите специализацию!' },
                            { min: 3, message: 'Специализация должна содержать не менее 3 символов' }
                        ]}
                    >
                        <Input placeholder="Введите специализацию" />
                    </Form.Item>
                </Form>
            </Modal>
        </Spin>
    );
};

export default DoctorsPage;