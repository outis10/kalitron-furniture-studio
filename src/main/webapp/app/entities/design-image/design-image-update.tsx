import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';
import { ImageType } from 'app/shared/model/enumerations/image-type.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './design-image.reducer';

export const DesignImageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designSessions = useAppSelector(state => state.designSession.entities);
  const designImageEntity = useAppSelector(state => state.designImage.entity);
  const loading = useAppSelector(state => state.designImage.loading);
  const updating = useAppSelector(state => state.designImage.updating);
  const updateSuccess = useAppSelector(state => state.designImage.updateSuccess);
  const imageTypeValues = Object.keys(ImageType);

  const handleClose = () => {
    navigate('/design-image');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDesignSessions({}));
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
    if (values.fileSizeKb !== undefined && typeof values.fileSizeKb !== 'number') {
      values.fileSizeKb = Number(values.fileSizeKb);
    }
    if (values.widthPx !== undefined && typeof values.widthPx !== 'number') {
      values.widthPx = Number(values.widthPx);
    }
    if (values.heightPx !== undefined && typeof values.heightPx !== 'number') {
      values.heightPx = Number(values.heightPx);
    }
    values.uploadedAt = convertDateTimeToServer(values.uploadedAt);

    const entity = {
      ...designImageEntity,
      ...values,
      session: designSessions.find(it => it.id.toString() === values.session?.toString()),
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
          uploadedAt: displayDefaultDateTime(),
        }
      : {
          imageType: 'REFERENCE',
          ...designImageEntity,
          uploadedAt: convertDateTimeFromServer(designImageEntity.uploadedAt),
          session: designImageEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.designImage.home.createOrEditLabel" data-cy="DesignImageCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.designImage.home.createOrEditLabel">Create or edit a DesignImage</Translate>
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
                  id="design-image-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.imageType')}
                id="design-image-imageType"
                name="imageType"
                data-cy="imageType"
                type="select"
              >
                {imageTypeValues.map(imageType => (
                  <option value={imageType} key={imageType}>
                    {translate(`kalitronFurnitureStudioApp.ImageType.${imageType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.fileName')}
                id="design-image-fileName"
                name="fileName"
                data-cy="fileName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.filePath')}
                id="design-image-filePath"
                name="filePath"
                data-cy="filePath"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.mimeType')}
                id="design-image-mimeType"
                name="mimeType"
                data-cy="mimeType"
                type="text"
                validate={{
                  maxLength: { value: 80, message: translate('entity.validation.maxlength', { max: 80 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.fileSizeKb')}
                id="design-image-fileSizeKb"
                name="fileSizeKb"
                data-cy="fileSizeKb"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.widthPx')}
                id="design-image-widthPx"
                name="widthPx"
                data-cy="widthPx"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.heightPx')}
                id="design-image-heightPx"
                name="heightPx"
                data-cy="heightPx"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.isActive')}
                id="design-image-isActive"
                name="isActive"
                data-cy="isActive"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.uploadedAt')}
                id="design-image-uploadedAt"
                name="uploadedAt"
                data-cy="uploadedAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designImage.description')}
                id="design-image-description"
                name="description"
                data-cy="description"
                type="text"
                validate={{
                  maxLength: { value: 300, message: translate('entity.validation.maxlength', { max: 300 }) },
                }}
              />
              <ValidatedField
                id="design-image-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.designImage.session')}
                type="select"
                required
              >
                <option value="" key="0" />
                {designSessions
                  ? designSessions.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.sessionCode}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/design-image" replace variant="info">
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

export default DesignImageUpdate;
