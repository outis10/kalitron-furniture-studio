import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { CabinetCategory } from 'app/shared/model/enumerations/cabinet-category.model';

import { createEntity, getEntity, reset, updateEntity } from './cabinet-template.reducer';

export const CabinetTemplateUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cabinetTemplateEntity = useAppSelector(state => state.cabinetTemplate.entity);
  const loading = useAppSelector(state => state.cabinetTemplate.loading);
  const updating = useAppSelector(state => state.cabinetTemplate.updating);
  const updateSuccess = useAppSelector(state => state.cabinetTemplate.updateSuccess);
  const cabinetCategoryValues = Object.keys(CabinetCategory);

  const handleClose = () => {
    navigate('/cabinet-template');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.defaultWidthMm !== undefined && typeof values.defaultWidthMm !== 'number') {
      values.defaultWidthMm = Number(values.defaultWidthMm);
    }
    if (values.defaultHeightMm !== undefined && typeof values.defaultHeightMm !== 'number') {
      values.defaultHeightMm = Number(values.defaultHeightMm);
    }
    if (values.defaultDepthMm !== undefined && typeof values.defaultDepthMm !== 'number') {
      values.defaultDepthMm = Number(values.defaultDepthMm);
    }
    if (values.minWidthMm !== undefined && typeof values.minWidthMm !== 'number') {
      values.minWidthMm = Number(values.minWidthMm);
    }
    if (values.maxWidthMm !== undefined && typeof values.maxWidthMm !== 'number') {
      values.maxWidthMm = Number(values.maxWidthMm);
    }
    if (values.sortOrder !== undefined && typeof values.sortOrder !== 'number') {
      values.sortOrder = Number(values.sortOrder);
    }

    const entity = {
      ...cabinetTemplateEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          category: 'UPPER',
          ...cabinetTemplateEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.cabinetTemplate.home.createOrEditLabel" data-cy="CabinetTemplateCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.cabinetTemplate.home.createOrEditLabel">
              Create or edit a CabinetTemplate
            </Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="cabinet-template-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.code')}
                id="cabinet-template-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 40, message: translate('entity.validation.maxlength', { max: 40 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.name')}
                id="cabinet-template-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.category')}
                id="cabinet-template-category"
                name="category"
                data-cy="category"
                type="select"
              >
                {cabinetCategoryValues.map(cabinetCategory => (
                  <option value={cabinetCategory} key={cabinetCategory}>
                    {translate(`kalitronFurnitureStudioApp.CabinetCategory.${cabinetCategory}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.defaultWidthMm')}
                id="cabinet-template-defaultWidthMm"
                name="defaultWidthMm"
                data-cy="defaultWidthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.defaultHeightMm')}
                id="cabinet-template-defaultHeightMm"
                name="defaultHeightMm"
                data-cy="defaultHeightMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.defaultDepthMm')}
                id="cabinet-template-defaultDepthMm"
                name="defaultDepthMm"
                data-cy="defaultDepthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.minWidthMm')}
                id="cabinet-template-minWidthMm"
                name="minWidthMm"
                data-cy="minWidthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.maxWidthMm')}
                id="cabinet-template-maxWidthMm"
                name="maxWidthMm"
                data-cy="maxWidthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.supportsDoors')}
                id="cabinet-template-supportsDoors"
                name="supportsDoors"
                data-cy="supportsDoors"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.supportsDrawers')}
                id="cabinet-template-supportsDrawers"
                name="supportsDrawers"
                data-cy="supportsDrawers"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.supportsShelves')}
                id="cabinet-template-supportsShelves"
                name="supportsShelves"
                data-cy="supportsShelves"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.fusionTemplateName')}
                id="cabinet-template-fusionTemplateName"
                name="fusionTemplateName"
                data-cy="fusionTemplateName"
                type="text"
                validate={{
                  maxLength: { value: 120, message: translate('entity.validation.maxlength', { max: 120 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.csvProfileJson')}
                id="cabinet-template-csvProfileJson"
                name="csvProfileJson"
                data-cy="csvProfileJson"
                type="textarea"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.isActive')}
                id="cabinet-template-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.cabinetTemplate.sortOrder')}
                id="cabinet-template-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/cabinet-template" replace variant="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button variant="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CabinetTemplateUpdate;
