import React, { useEffect } from 'react';
import { Button, Col, FormText, Row } from 'react-bootstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { Link, useNavigate, useParams } from 'react-router';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities as getDesignSessions } from 'app/entities/design-session/design-session.reducer';
import { ArtifactType } from 'app/shared/model/enumerations/artifact-type.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';

import { createEntity, getEntity, reset, updateEntity } from './design-artifact.reducer';

export const DesignArtifactUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const designSessions = useAppSelector(state => state.designSession.entities);
  const designArtifactEntity = useAppSelector(state => state.designArtifact.entity);
  const loading = useAppSelector(state => state.designArtifact.loading);
  const updating = useAppSelector(state => state.designArtifact.updating);
  const updateSuccess = useAppSelector(state => state.designArtifact.updateSuccess);
  const artifactTypeValues = Object.keys(ArtifactType);

  const handleClose = () => {
    navigate(`/design-artifact${location.search}`);
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
    values.createdAt = convertDateTimeToServer(values.createdAt);

    const entity = {
      ...designArtifactEntity,
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
          createdAt: displayDefaultDateTime(),
        }
      : {
          artifactType: 'CSV',
          ...designArtifactEntity,
          createdAt: convertDateTimeFromServer(designArtifactEntity.createdAt),
          session: designArtifactEntity?.session?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kalitronFurnitureStudioApp.designArtifact.home.createOrEditLabel" data-cy="DesignArtifactCreateUpdateHeading">
            <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.home.createOrEditLabel">
              Create or edit a DesignArtifact
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
                  id="design-artifact-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              )}
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.artifactType')}
                id="design-artifact-artifactType"
                name="artifactType"
                data-cy="artifactType"
                type="select"
              >
                {artifactTypeValues.map(artifactType => (
                  <option value={artifactType} key={artifactType}>
                    {translate(`kalitronFurnitureStudioApp.ArtifactType.${artifactType}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.fileName')}
                id="design-artifact-fileName"
                name="fileName"
                data-cy="fileName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 255, message: translate('entity.validation.maxlength', { max: 255 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.filePath')}
                id="design-artifact-filePath"
                name="filePath"
                data-cy="filePath"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.mimeType')}
                id="design-artifact-mimeType"
                name="mimeType"
                data-cy="mimeType"
                type="text"
                validate={{
                  maxLength: { value: 80, message: translate('entity.validation.maxlength', { max: 80 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.fileSizeKb')}
                id="design-artifact-fileSizeKb"
                name="fileSizeKb"
                data-cy="fileSizeKb"
                type="text"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.checksum')}
                id="design-artifact-checksum"
                name="checksum"
                data-cy="checksum"
                type="text"
                validate={{
                  maxLength: { value: 128, message: translate('entity.validation.maxlength', { max: 128 }) },
                }}
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.metadataJson')}
                id="design-artifact-metadataJson"
                name="metadataJson"
                data-cy="metadataJson"
                type="textarea"
              />
              <ValidatedField
                label={translate('kalitronFurnitureStudioApp.designArtifact.createdAt')}
                id="design-artifact-createdAt"
                name="createdAt"
                data-cy="createdAt"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                id="design-artifact-session"
                name="session"
                data-cy="session"
                label={translate('kalitronFurnitureStudioApp.designArtifact.session')}
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
              <Button as={Link as any} id="cancel-save" data-cy="entityCreateCancelButton" to="/design-artifact" replace variant="info">
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

export default DesignArtifactUpdate;
