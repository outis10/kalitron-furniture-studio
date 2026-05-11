import React, { useEffect, useState } from 'react';
import { Button, Table } from 'react-bootstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { Link, useLocation, useNavigate } from 'react-router';

import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';

import { getEntities } from './design-artifact.reducer';

export const DesignArtifact = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const designArtifactList = useAppSelector(state => state.designArtifact.entities);
  const loading = useAppSelector(state => state.designArtifact.loading);
  const totalItems = useAppSelector(state => state.designArtifact.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="design-artifact-heading" data-cy="DesignArtifactHeading">
        <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.home.title">Design Artifacts</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" variant="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/design-artifact/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.home.createLabel">Create new Design Artifact</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {designArtifactList?.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('artifactType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.artifactType">Artifact Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('artifactType')} />
                </th>
                <th className="hand" onClick={sort('fileName')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.fileName">File Name</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileName')} />
                </th>
                <th className="hand" onClick={sort('filePath')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.filePath">File Path</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('filePath')} />
                </th>
                <th className="hand" onClick={sort('mimeType')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.mimeType">Mime Type</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('mimeType')} />
                </th>
                <th className="hand" onClick={sort('fileSizeKb')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.fileSizeKb">File Size Kb</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('fileSizeKb')} />
                </th>
                <th className="hand" onClick={sort('checksum')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.checksum">Checksum</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('checksum')} />
                </th>
                <th className="hand" onClick={sort('metadataJson')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.metadataJson">Metadata Json</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('metadataJson')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.createdAt">Created At</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th>
                  <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.session">Session</Translate>{' '}
                  <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {designArtifactList.map(designArtifact => (
                <tr key={`entity-${designArtifact.id}`} data-cy="entityTable">
                  <td>
                    <Button as={Link as any} to={`/design-artifact/${designArtifact.id}`} variant="link" size="sm">
                      {designArtifact.id}
                    </Button>
                  </td>
                  <td>
                    <Translate contentKey={`kalitronFurnitureStudioApp.ArtifactType.${designArtifact.artifactType}`} />
                  </td>
                  <td>{designArtifact.fileName}</td>
                  <td>{designArtifact.filePath}</td>
                  <td>{designArtifact.mimeType}</td>
                  <td>{designArtifact.fileSizeKb}</td>
                  <td>{designArtifact.checksum}</td>
                  <td>{designArtifact.metadataJson}</td>
                  <td>
                    {designArtifact.createdAt ? <TextFormat type="date" value={designArtifact.createdAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {designArtifact.session ? (
                      <Link to={`/design-session/${designArtifact.session.id}`}>{designArtifact.session.sessionCode}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        as={Link as any}
                        to={`/design-artifact/${designArtifact.id}`}
                        variant="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        as={Link as any}
                        to={`/design-artifact/${designArtifact.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        variant="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/design-artifact/${designArtifact.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        variant="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="kalitronFurnitureStudioApp.designArtifact.home.notFound">No Design Artifacts found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={designArtifactList && designArtifactList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default DesignArtifact;
