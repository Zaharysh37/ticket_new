import React, { useState, useEffect } from 'react';
import { Table, Button, Modal, Form, Input, message, Spin, Popconfirm, Empty } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined, ClearOutlined } from '@ant-design/icons';
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
    const [filters, setFilters] = useState({
        name: null,
        specialization: null
    });

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

    const checkDoctorExists = (name, specialization, excludeId = null) => {
        return doctors.some(d =>
            d.name === name &&
            d.specialization === specialization &&
            d.id !== excludeId
        );
    };

    const handleCreate = async () => {
        try {
            const values = await form.validateFields();

            if (checkDoctorExists(values.name, values.specialization)) {
                message.error('Врач с такими ФИО и специализацией уже существует');
                return;
            }

            setLoading(true);
            await createDoctor(values);
            message.success('Врач добавлен успешно');
            resetModal();
            await fetchDoctors();
        } catch (error) {
            message.error('Ошибка добавления врача');
        } finally {
            setLoading(false);
        }
    };

    const handleUpdate = async () => {
        try {
            const values = await form.validateFields();

            if (checkDoctorExists(values.name, values.specialization, editingDoctorId)) {
                message.error('Врач с такими ФИО и специализацией уже существует');
                return;
            }

            setLoading(true);
            await updateDoctor(editingDoctorId, values);
            message.success('Данные врача обновлены успешно');
            resetModal();
            await fetchDoctors();
        } catch (error) {
            message.error('Ошибка обновления данных врача');
        } finally {
            setLoading(false);
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

    const handleTableChange = (pagination, filters) => {
        setFilters({
            name: filters.name || null,
            specialization: filters.specialization || null
        });
    };

    const resetAllFilters = () => {
        setFilters({
            name: null,
            specialization: null
        });
    };

    const columns = [
        {
            title: 'ФИО',
            dataIndex: 'name',
            key: 'name',
            width: 200,
            filters: [...new Set(doctors.map(d => d.name))].map(name => ({
                text: name,
                value: name,
            })),
            onFilter: (value, record) => record.name === value,
            filterSearchPlaceholder: 'Поиск',
            filteredValue: filters.name || null,
        },
        {
            title: 'Специализация',
            dataIndex: 'specialization',
            key: 'specialization',
            width: 200,
            filters: [...new Set(doctors.map(d => d.specialization))].map(spec => ({
                text: spec,
                value: spec,
            })),
            onFilter: (value, record) => record.specialization === value,
            filterSearchPlaceholder: 'Поиск',
            filteredValue: filters.specialization || null,
        },
        {
            title: 'Действия',
            key: 'actions',
            fixed: 'right',
            width: 55,
            align: 'center',
            render: (_, record) => (
                <div style={{
                    display: 'flex',
                    justifyContent: 'space-evenly',
                    width: '100%'
                }}>
                    <Button
                        type="text"
                        icon={<EditOutlined />}
                        onClick={() => handleEdit(record.id)}
                        style={{
                            color: '#1890ff',
                            minWidth: 24,
                            padding: 0
                        }}
                    />
                    <Popconfirm
                        title="Удалить врача?"
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
                                padding: 0
                            }}
                        />
                    </Popconfirm>
                </div>
            ),
        },
    ];

    return (
        <Spin spinning={loading}>
            <div style={{ marginBottom: 16, display: 'flex', gap: 8 }}>
                <Button
                    type="primary"
                    icon={<PlusOutlined />}
                    onClick={() => setIsModalVisible(true)}
                >
                    Добавить врача
                </Button>
                <Button
                    icon={<ClearOutlined />}
                    onClick={resetAllFilters}
                    disabled={!filters.name && !filters.specialization}
                >
                    Сбросить фильтры
                </Button>
            </div>

            <Table
                columns={columns}
                dataSource={doctors}
                rowKey="id"
                loading={loading}
                bordered
                pagination={{ pageSize: 10 }}
                scroll={{ x: 800 }}
                locale={{
                    filterReset: 'Сбросить',
                    filterConfirm: 'ОК',
                    emptyText: <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} description="Нет данных" />
                }}
                onChange={handleTableChange}
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
                        label="ФИО врача"
                        rules={[
                            { required: true, message: 'Пожалуйста, введите ФИО врача!' },
                            { min: 3, message: 'ФИО должно содержать не менее 3 символов' }
                        ]}
                    >
                        <Input placeholder="Введите ФИО врача" />
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