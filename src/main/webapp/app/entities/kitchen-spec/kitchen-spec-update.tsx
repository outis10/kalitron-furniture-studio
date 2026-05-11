import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getMaterials } from 'app/entities/material/material.reducer';
import { FinishType } from 'app/shared/model/enumerations/finish-type.model';
import { KitchenLayout } from 'app/shared/model/enumerations/kitchen-layout.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './kitchen-spec.reducer';

export const KitchenSpecUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const materials = useAppSelector(state => state.material.entities);
  const kitchenSpecEntity = useAppSelector(state => state.kitchenSpec.entity);
  const loading = useAppSelector(state => state.kitchenSpec.loading);
  const updating = useAppSelector(state => state.kitchenSpec.updating);
  const updateSuccess = useAppSelector(state => state.kitchenSpec.updateSuccess);
  const kitchenLayoutValues = Object.keys(KitchenLayout);
  const finishTypeValues = Object.keys(FinishType);

  const handleClose = () => {
    navigate('/kitchen-spec');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMaterials({}));
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
    if (values.totalWidthMm !== undefined && typeof values.totalWidthMm !== 'number') {
      values.totalWidthMm = Number(values.totalWidthMm);
    }
    if (values.totalHeightMm !== undefined && typeof values.totalHeightMm !== 'number') {
      values.totalHeightMm = Number(values.totalHeightMm);
    }
    if (values.totalDepthMm !== undefined && typeof values.totalDepthMm !== 'number') {
      values.totalDepthMm = Number(values.totalDepthMm);
    }
    values.confirmedAt = convertDateTimeToServer(values.confirmedAt);

    const entity = {
      ...kitchenSpecEntity,
      ...values,
      primaryMaterial: materials.find(it => it.id.toString() === values.primaryMaterial?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          confirmedAt: displayDefaultDateTime(),
        }
      : {
          layout: 'LINEAR',
          primaryFinish: 'MATTE_WHITE',
          ...kitchenSpecEntity,
          confirmedAt: convertDateTimeFromServer(kitchenSpecEntity.confirmedAt),
          primaryMaterial: kitchenSpecEntity?.primaryMaterial?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.kitchenSpec.home.createOrEditLabel" data-cy="KitchenSpecCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.kitchenSpec.home.createOrEditLabel">Create or edit a KitchenSpec</Translate>
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
                  id="kitchen-spec-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.layout')}
                id="kitchen-spec-layout"
                name="layout"
                data-cy="layout"
                type="select"
              >
                {kitchenLayoutValues.map(kitchenLayout => (
                  <option value={kitchenLayout} key={kitchenLayout}>
                    {translate(`kalitronFurnitureStudioApp.KitchenLayout.${kitchenLayout}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.totalWidthMm')}
                id="kitchen-spec-totalWidthMm"
                name="totalWidthMm"
                data-cy="totalWidthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.totalHeightMm')}
                id="kitchen-spec-totalHeightMm"
                name="totalHeightMm"
                data-cy="totalHeightMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.totalDepthMm')}
                id="kitchen-spec-totalDepthMm"
                name="totalDepthMm"
                data-cy="totalDepthMm"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.style')}
                id="kitchen-spec-style"
                name="style"
                data-cy="style"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 60, message: translate('entity.validation.maxlength', { max: 60 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.primaryFinish')}
                id="kitchen-spec-primaryFinish"
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
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.handleType')}
                id="kitchen-spec-handleType"
                name="handleType"
                data-cy="handleType"
                type="text"
                validate={{
                  maxLength: { value: 80, message: translate('entity.validation.maxlength', { max: 80 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.countertopMaterial')}
                id="kitchen-spec-countertopMaterial"
                name="countertopMaterial"
                data-cy="countertopMaterial"
                type="text"
                validate={{
                  maxLength: { value: 80, message: translate('entity.validation.maxlength', { max: 80 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.sinkPosition')}
                id="kitchen-spec-sinkPosition"
                name="sinkPosition"
                data-cy="sinkPosition"
                type="text"
                validate={{
                  maxLength: { value: 80, message: translate('entity.validation.maxlength', { max: 80 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.confirmedByClient')}
                id="kitchen-spec-confirmedByClient"
                name="confirmedByClient"
                data-cy="confirmedByClient"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.extractedJson')}
                id="kitchen-spec-extractedJson"
                name="extractedJson"
                data-cy="extractedJson"
                type="textarea"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.confirmedAt')}
                id="kitchen-spec-confirmedAt"
                name="confirmedAt"
                data-cy="confirmedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="kitchen-spec-primaryMaterial"
                name="primaryMaterial"
                data-cy="primaryMaterial"
                label={translate('kalitronFurnitureStudioApp.kitchenSpec.primaryMaterial')}
                type="select"
                required
              >
                <option value="" key="0" />
                {materials
                  ? materials.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/kitchen-spec" replace variant="info">
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

export default KitchenSpecUpdate;
