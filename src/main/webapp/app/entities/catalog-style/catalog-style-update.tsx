import React, { useEffect } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { FinishType } from 'app/shared/model/enumerations/finish-type.model';

import { createEntity, getEntity, reset, updateEntity } from './catalog-style.reducer';

export const CatalogStyleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const catalogStyleEntity = useAppSelector(state => state.catalogStyle.entity);
  const loading = useAppSelector(state => state.catalogStyle.loading);
  const updating = useAppSelector(state => state.catalogStyle.updating);
  const updateSuccess = useAppSelector(state => state.catalogStyle.updateSuccess);
  const finishTypeValues = Object.keys(FinishType);

  const handleClose = () => {
    navigate('/catalog-style');
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
    if (values.sortOrder !== undefined && typeof values.sortOrder !== 'number') {
      values.sortOrder = Number(values.sortOrder);
    }

    const entity = {
      ...catalogStyleEntity,
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
          primaryFinish: 'MATTE_WHITE',
          ...catalogStyleEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.catalogStyle.home.createOrEditLabel" data-cy="CatalogStyleCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.catalogStyle.home.createOrEditLabel">Create or edit a CatalogStyle</Translate>
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
                  id="catalog-style-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.name')}
                id="catalog-style-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 100, message: translate('entity.validation.maxlength', { max: 100 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.description')}
                id="catalog-style-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.thumbnailPath')}
                id="catalog-style-thumbnailPath"
                name="thumbnailPath"
                data-cy="thumbnailPath"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.style')}
                id="catalog-style-style"
                name="style"
                data-cy="style"
                type="text"
                validate={{
                  maxLength: { value: 60, message: translate('entity.validation.maxlength', { max: 60 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.primaryFinish')}
                id="catalog-style-primaryFinish"
                name="primaryFinish"
                data-cy="primaryFinish"
                type="select"
              >
                {finishTypeValues.map(finishType => (
                  <option value={finishType} key={finishType}>
                    {translate(`kalitronFurnitureStudioApp.FinishType.${finishType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.priceRange')}
                id="catalog-style-priceRange"
                name="priceRange"
                data-cy="priceRange"
                type="text"
                validate={{
                  maxLength: { value: 30, message: translate('entity.validation.maxlength', { max: 30 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.isActive')}
                id="catalog-style-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.catalogStyle.sortOrder')}
                id="catalog-style-sortOrder"
                name="sortOrder"
                data-cy="sortOrder"
                type="text"
              />
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/catalog-style" replace variant="info">
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

export default CatalogStyleUpdate;
